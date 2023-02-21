import {showMessage} from "../util.js";

$(document).ready(async () => {
    let body=$('body');
    let isFollower = body.attr("data-is-follower");
    let reporterId = body.attr("data-reporter-id");

    let unfollowBtn = $("#unfollow-button");
    let followBtn = $("#follow-button");
    let writePostBtn = $("#write-post");

    unfollowBtn.click( function(){
        unfollowReporter(reporterId);
    });

    followBtn.click( function(){
        followReporter(reporterId);
    });

    writePostBtn.click( function(){
        publishNewPost(reporterId);
    });

    if(isFollower === "true"){
        unfollowBtn.show();
    }else{
        followBtn.show();
    }
});

function publishNewPost(reporterID) {
    let textarea = $("textarea").val();
    let hashtags = $("#hashtags-input").val();
    let links = $("#related-links-input").val();
    $.post(
        "posthandling",
        {
            reporterID: reporterID,
            operation: "insert",
            text: textarea,
            hashtags: hashtags,
            links: links
        },
        function(newPostJson) {
            cleanNewPostForm();
            try {
                let postID = newPostJson["reporterId"];
                let textarea = newPostJson["text"];
                let hashtags = newPostJson["hashtags"];
                let links = newPostJson["links"];
                createNewPost(reporterID, postID, textarea, hashtags, links);
            } catch (e) {
                console.error("Error parsing JSON: " + e);
            }
        }
    ).fail(function(jqXHR, textStatus, errorThrown) {
        console.error("AJAX request failed: " + errorThrown);
    });
}

function cleanNewPostForm() {
    $("textarea").val("");
    $("#hashtags-input").val("");
    $("#related-links-input").val("");
}

function createNewPost(reporterID, postID, text, hashtags, links) {

    var newPost = $("<div></div>");
    newPost.attr({
       "class": "post-container container my-5 search-result",
        "id": postID,
    });

    var postHeader = $("<header></header>");
    postHeader.attr("class","post-header");
    newPost.append(postHeader);

    var removeSpan = $("<span></span>")
    removeSpan.attr("class","option");
    removeSpan.attr("data-ref", postID);
    postHeader.append(removeSpan);

    var removeIcon = $("<i></i>");
    removeIcon.attr("class","bi bi-trash3");
    removeIcon.click(removePost(reporterID, postID));
    removeSpan.append(removeIcon);

    newPost.append($("<hr>"));

    var postText = $("<p>" + text + "</p>");
    postText.attr("class","post-text");
    newPost.append(postText);

    var postFooter = $("<footer></footer>");
    newPost.append(postFooter);

    var hashtagsString = hashtags.toString();
    if(hashtagsString !== "") {
        hashtagsString = hashtagsString.replace(/,/g, " ");

        var postHashtags = $("<p>" + hashtagsString + "</p>");
        postHashtags.attr("class","hashtags");
        postFooter.append(postHashtags);
    }

    var linksString = links.toString();
    if(linksString !== "") {
        linksString = linksString.replace(/,/g, " ");

        var postLinks = $("<p>" + linksString + "</p>");
        postLinks.attr("class","related-links");
        postFooter.append(postLinks);
    }

    var postTimestamp = $("<p>Now</p>");
    postTimestamp.attr("class","timestamp");
    postFooter.append(postTimestamp);

    postFooter.append($("<hr>"));

    var showCommentsDiv = $("<div></div>");
    showCommentsDiv.attr("class","show-comm-div");
    postFooter.append(showCommentsDiv);

    var showCommentsButton = $("<button>Show comments</button>");
    showCommentsButton.attr("class","show-comm");
    showCommentsDiv.append(showCommentsButton);

    $("#post-items").prepend(newPost);
}

function removePost(reporterID, postID) {
    $.post(
        "posthandling",
        {
            reporterID: reporterID,
            operation: "remove",
            postID: postID
        }
    ).done(function() {
        hideRemovedPost(postID);
    }).fail(function(jqXHR, textStatus, errorThrown) {
        console.error("AJAX request failed: " + errorThrown);
    });
}

function hideRemovedPost(postID) {
    $("#" + postID).remove();
}

function followReporter (reporterId){
    $.post(
        "reporterPage",
        {
            operation: "follow",
            reporterId: reporterId
        },
        function() {
            showMessage('success', 'Follow operation successfully executed');
            $("#follow-button").hide();
            $("#unfollow-button").show();
        }).fail(function() {
            showMessage('danger', `Something went wrong. Please retry later`);
        });
}

function unfollowReporter (reporterId){
    $.post(
        "reporterPage",
        {
            operation: "unfollow",
            reporterId: reporterId
        },
        function() {
            showMessage('success', 'Unfollow operation successfully executed');
            $("#unfollow-button").hide();
            $("#follow-button").show();
        }).fail(function() {
            showMessage('danger', `Something went wrong. Please retry later`);
        });
}