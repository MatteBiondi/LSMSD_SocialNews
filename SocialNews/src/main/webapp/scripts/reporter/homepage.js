import {showMessage} from "../util.js";

$(document).ready(async () => {
    let body=$('body');
    let isFollower = body.attr("data-is-follower");
    let reporterId = body.attr("data-reporter-id");

    let unfollowBtn = $("#unfollow-button");
    let followBtn = $("#follow-button");
    let writePostBtn = $("#write-post");
    let removeButton = $(".remove-button");
    let showCommentsBtn = $(".show-comm");
    let writeCommentBtn = $(".write-comment");

    unfollowBtn.click( function(){
        unfollowReporter(reporterId);
    });

    followBtn.click( function(){
        followReporter(reporterId);
    });

    writePostBtn.click( function(){
        publishNewPost(reporterId);
    });

    removeButton.click( function(event) {
        let target = event.currentTarget;
        let postId = $(target).attr("data-post");
        let reporter = $(target).attr("data-reporter");
        removePost(reporter, postId);
    });

    showCommentsBtn.click( function(event) {
        let target = event.currentTarget;
        let postId = $(target).attr("data-post");
        let reporter = $(target).attr("data-reporter");
        let userId = $("body").attr("data-user-id");
        showComments(reporter, postId, userId);
    });

    writeCommentBtn.click( function(event) {
        let postId = $(this).closest('.post-container').attr('id');
        let readerId = $("body").attr("data-user-id");
        let target = event.currentTarget;
        let reporterId = $(target).attr("data-reporter");
        publishNewComment(postId, readerId, reporterId, readerId);
    })

    if(isFollower === "true"){
        unfollowBtn.show();
    }else{
        followBtn.show();
    }
});

function publishNewPost(reporterID) {
    let textarea = $("#new-post-textarea").val();
    let hashtags = $("#hashtags-input").val();
    let links = $("#related-links-input").val();

    // Get root of current pathname
    let rootPathname = window.location.pathname.match(/^\/[^/]+/)[0];
    let baseUrl = window.location.origin + rootPathname ;

    $.post(
        `${baseUrl}/posthandling`,
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
                let timestamp = newPostJson["timestamp"];
                createNewPost(reporterID, postID, textarea, hashtags, links, timestamp, "Now");
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

function createNewPost(reporterID, postID, text, hashtags, links, timestamp, formattedTimestamp) {

    let newPost = $("<div></div>");
    newPost.attr({
        "class": "post-container container my-5 search-result",
        "id": postID,
        "data-millis-time": timestamp
    });

    let postHeader = $("<header></header>");
    postHeader.attr("class","post-header");
    newPost.append(postHeader);

    let removeSpan = $("<span></span>")
    removeSpan.attr("class","option");
    removeSpan.attr("data-ref", postID);
    postHeader.append(removeSpan);

    let removeIcon = $("<i></i>");
    removeIcon.attr("class","bi bi-trash3");
    removeIcon.click(function() {
        removePost(reporterID, postID);
    });
    removeSpan.append(removeIcon);

    newPost.append($("<hr>"));

    let postText = $("<p>" + text + "</p>");
    postText.attr("class","post-text");
    newPost.append(postText);

    let postFooter = $("<footer></footer>");
    newPost.append(postFooter);

    let hashtagsString = hashtags.toString();
    if(hashtagsString !== "") {
        hashtagsString = hashtagsString.replace(/,/g, " ");

        let postHashtags = $("<p>" + hashtagsString + "</p>");
        postHashtags.attr("class","hashtags");
        postFooter.append(postHashtags);
    }

    let linksString = links.toString();
    if(linksString !== "") {
        linksString = linksString.replace(/,/g, " ");

        let postLinks = $("<p>" + linksString + "</p>");
        postLinks.attr("class","related-links");
        postFooter.append(postLinks);
    }

    let postTimestamp = $("<p>" + formattedTimestamp + "</p>"); //fixme: problema -> non ho l'attributo data-millis-time. Controllare e vedere dove andava messo.
    postTimestamp.attr("class","timestamp");
    postFooter.append(postTimestamp);

    postFooter.append($("<hr>"));

    let showCommentsDiv = $("<div></div>");
    showCommentsDiv.attr("class","show-comm-div");
    postFooter.append(showCommentsDiv);

    let showCommentsButton = $("<button>Show comments</button>");
    showCommentsButton.attr("class","show-comm");
    showCommentsDiv.append(showCommentsButton);

    $("#post-items").prepend(newPost);
}

function removePost(reporterID, postID) {
    // Get root of current pathname
    let rootPathname = window.location.pathname.match(/^\/[^/]+/)[0];
    let baseUrl = window.location.origin + rootPathname ;


    console.log("removePost");
    $.post(
        `${baseUrl}/posthandling`,
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
            let numFollower = parseInt($("#followers-number").text()) + 1;
            $("#followers-number").text(numFollower);
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
            let numFollower = parseInt($("#followers-number").text()) - 1;
            $("#followers-number").text(numFollower);
        }).fail(function() {
        showMessage('danger', `Something went wrong. Please retry later`);
    });
}


/********** Post paging **********/
const POST_PER_PAGE = 25;

$(document).ready(async () => {
    sessionStorage.setItem("page","1");
    sessionStorage.setItem("lastId", "");
    sessionStorage.setItem("lastTimestamp", "");

    // Retrieve number of first page posts and evaluate next page button
    nextPaging($(".search-result").length);


    $("#previous").on(
        "click",
        () => {
            let page = parseInt(sessionStorage.getItem("page"));
            sessionStorage.setItem("page", (page-1).toString());
            takeLastPost("previous");

            pageRequest("previous");
        }
    );

    $("#next").on(
        "click",
        () => {
            let page = parseInt(sessionStorage.getItem("page"));
            sessionStorage.setItem("page", (page+1).toString());
            takeLastPost("next");

            pageRequest("next");
        }
    );
});

function takeLastPost(direction){
    let lastPost;

    if(direction === "previous")
        lastPost = $("#post-items").children().first();
    else
        lastPost = $("#post-items").children().last();

    console.log("takeLastPost");
    console.log(lastPost);
    sessionStorage.setItem("lastId", lastPost.attr('id'));
    sessionStorage.setItem("lastTimestamp", lastPost.attr("data-millis-time"));
}

async function pageRequest(direction){
    let lastId = sessionStorage.getItem("lastId");
    let lastTimestamp = sessionStorage.getItem("lastTimestamp");

    console.log("Page request");

    // Get root of current pathname
    let rootPathname = window.location.pathname.match(/^\/[^/]+/)[0];
    let baseUrl = window.location.origin + rootPathname ;

    let newPostsListJSON = await $.get(
        `${baseUrl}/posthandling?direction=${direction}&lastId=${lastId}&lastTimestamp=${lastTimestamp}`
    );

    let newPostsList = JSON.parse(newPostsListJSON);

    //if(direction==="previous")
    newPostsList.reverse();

    console.log(newPostsList);

    loadNewPage(newPostsList);
}

function cleanDashboard() {
    $("#post-items").empty();
}

function loadNewPage(newPostsList) {

    let numPost;

    console.log("loadNewPage");

    if(newPostsList.length > 0) {
        cleanDashboard();

        console.log("length risultato > 0");

        let body=$('body');
        let reporterId = body.attr("data-reporter-id");

        $.each(newPostsList, function(index, postJSON) {
            let postId = postJSON["id"];
            let text = postJSON["text"];

            let hashtagField = postJSON["hashtags"];
            let hashtags = hashtagField === undefined ? "":hashtagField;

            let linkField = postJSON["links"];
            let links = linkField === undefined? "":linkField;

            let timestamp = postJSON["timestamp"];
            let formattedTimestamp = getFormattedTimestamp(timestamp);

            createNewPost(reporterId, postId, text, hashtags, links, timestamp, formattedTimestamp);
        });
        numPost = newPostsList.length;
    }
    else {
        numPost = 0;
        let page = parseInt(sessionStorage.getItem("page"));
        sessionStorage.setItem("page", (page-1).toString());
    }

    nextPaging(numPost);
    previousPaging();
}

function getFormattedTimestamp(milliseconds) {
    let date = moment(milliseconds);
    return date.format("ddd MMM DD HH:mm:ss [CET] YYYY");
}

function nextPaging(numPosts) {
    if(numPosts < POST_PER_PAGE)
        $("#next-button").addClass("disabled");
    else
        $("#next-button").removeClass("disabled");

    console.log("nextPaging");
}

function previousPaging() {
    let page = parseInt(sessionStorage.getItem("page"));
    if( page < 2 )
        $("#previous-button").addClass("disabled");
    else
        $("#previous-button").removeClass("disabled");
}

/********** Show comments functionalities **********/

async function showComments(reporterId, postId, userId) {
    // Get root of current pathname
    let rootPathname = window.location.pathname.match(/^\/[^/]+/)[0];
    let baseUrl = window.location.origin + rootPathname ;

    let commentsFromPostJSON = await $.get(
        `${baseUrl}/commenthandling?page=1&reporterId=${reporterId}&postId=${postId}`
    );

    let commentsFromPost = JSON.parse(commentsFromPostJSON);

    if(commentsFromPost.length > 0) {
        loadComments(reporterId, postId, commentsFromPost, userId);

        let postDiv = document.getElementById(postId);
        let showCommBtn = postDiv.querySelector("button.show-comm");
        showCommBtn.remove();
        let showMoreBtn = $("<button>Show more</button>");
        showMoreBtn.attr("class","show-comm");
        showMoreBtn.on(
            "click",
            () => {
                takeLastComment(postId);
                commentsPageRequest(postId);
            }
        );

        let showCommDiv = postDiv.querySelector("div.show-comm-div");
        showCommDiv.append(showMoreBtn[0]);

        let numComments = commentsFromPost.length;
        nextCommentsPaging(numComments, postId);

        /*Set the comments page to 1*/
        showCommDiv.setAttribute("page","1");
    }
    else
        loadNoCommentsMessage(postId);
}

function loadNoCommentsMessage(postId) {
    let postDiv = document.getElementById(postId);
    let commentsDiv = postDiv.querySelector("div.show-comm-div");

    let message = $("<p>No comments yet.</p>");
    message.attr("class","empty-data-msg");
    commentsDiv.append(message[0]);

    let showCommentsButton = postDiv.querySelector("button.show-comm");
    showCommentsButton.remove();
}

function loadComments(reporterId, postId, commentsFromPost, userId) {

    $.each(commentsFromPost, function(index, comment) {

        let commentId = comment["id"];
        let timestamp = comment["timestamp"];
        let formattedTimestamp = getFormattedTimestamp(timestamp);
        let readerName = comment["reader"]["fullName"];
        let readerId = comment["reader"]["id"];
        let commentText = comment["text"];
        createNewComment(postId, commentId, readerId, readerName, commentText, timestamp, formattedTimestamp, userId);
    })
}

function publishNewComment(postId, readerId, reporterId, userId) {
    let postDiv = document.getElementById(postId);
    let textarea = postDiv.querySelector(".new-comment-textarea");
    let commentText = textarea.value;

    // Get root of current pathname
    let rootPathname = window.location.pathname.match(/^\/[^/]+/)[0];
    let baseUrl = window.location.origin + rootPathname ;

    $.post(
        `${baseUrl}/commenthandling`,
        {
            postId: postId,
            operation: "insert",
            reporterId: reporterId,
            text: commentText,
        },
        function(newCommentJson) {
            textarea.value = "";
            try {
                let commentId = newCommentJson["commentId"];
                let timestamp = newCommentJson["timestamp"];
                let readerName = newCommentJson["reader"]["fullName"];
                createNewComment(postId, commentId, readerId, readerName, commentText, timestamp, "Now", userId);
            } catch (e) {
                console.error("Error parsing JSON: " + e);
            }
        }
    ).fail(function(jqXHR, textStatus, errorThrown) {
        console.error("AJAX request failed: " + errorThrown);
    });
}

function createNewComment(postId, commentId, readerId, readerName, text, timestamp, formattedTimestamp, userId) {
    let newComment = $("<div></div>");
    newComment.attr({
        "class": "comment-container container my-5",
        "id": commentId,
        "data-millis-time": timestamp
    });

    let commentHeader = $("<header></header>");
    commentHeader.attr("class","post-header");
    newComment.append(commentHeader);

    let removeSpan = $("<span></span>")
    removeSpan.attr("class","option");
    removeSpan.attr("data-ref", commentId);
    commentHeader.append(removeSpan);

    if(userId === readerId) {
        let removeIcon = $("<i></i>");
        removeIcon.attr("class","bi bi-trash3");
        removeIcon.click(function() {
            removeComment(commentId);
        });
        removeSpan.append(removeIcon);
    }

    let reader = $("<h3>" + readerName + "</h3>");
    reader.attr("class", "reader-name");
    newComment.append(reader);

    let commentText = $("<p>" + text + "</p>");
    commentText.attr("class","comment-text");
    newComment.append(commentText);

    let commentFooter = $("<footer></footer>");
    newComment.append(commentFooter);

    let commentTimestamp = $("<p>" + formattedTimestamp + "</p>");
    commentTimestamp.attr("class","timestamp");
    commentFooter.append(commentTimestamp);

    let postDiv = document.getElementById(postId);
    let commentsDiv = postDiv.querySelector("div.show-comm-div");
    commentsDiv.prepend(newComment[0]);
}

function removeComment(commentId) {
    // Get root of current pathname
    let rootPathname = window.location.pathname.match(/^\/[^/]+/)[0];
    let baseUrl = window.location.origin + rootPathname ;

    $.post(
        `${baseUrl}/commenthandling`,
        {
            operation: "remove",
            commentId: commentId
        }
    ).done(function() {
        hideRemovedComment(commentId);
    }).fail(function(jqXHR, textStatus, errorThrown) {
        console.error("AJAX request failed: " + errorThrown);
    });
}

function hideRemovedComment(commentId) {
    $("#" + commentId).remove();
}


/********** Comments Paging **********/

const COMMENTS_PER_PAGE = 20;

function takeLastComment(postId){
    let postDiv = document.getElementById("32c5847d-df39-4c88-8cb5-ed0a6afa30bd");
    let showCommDiv = postDiv.getElementsByClassName("comment-container");
    let lastComment = showCommDiv[showCommDiv.length-1];
    console.log(lastComment);

    //todo: salvare le cose in altro modo
    console.log("takeLastComment");
    sessionStorage.setItem("commentsLastId", lastComment.getAttribute("id"));
    sessionStorage.setItem("commentsLastTimestamp", lastComment.getAttribute("data-millis-time"));
}

async function commentsPageRequest(postId){
    let lastId = sessionStorage.getItem("commentsLastId");
    let lastTimestamp = sessionStorage.getItem("commentsLastTimestamp");

    let postDiv = document.getElementById(postId);
    let showCommDiv = postDiv.querySelector("div.show-comm-div");
    let page = parseInt(showCommDiv.getAttribute("page")) + 1;

    console.log("Comments Page request");

    let urlOrigin = window.location.origin;
    // Get root of current pathname
    let rootPathname = window.location.pathname.match(/^\/[^/]+/)[0];

    //Last value is timestamp for post and fullName for reporter
    let newCommentsListJSON = await $.get(
        `${urlOrigin}/${rootPathname}/commenthandling?page=${page}&postId=${postId}&commentsLastId=${lastId}&commentsLastTimestamp=${lastTimestamp}`
    );

    let newCommentsList = JSON.parse(newCommentsListJSON);

    //if(direction==="previous")
    newCommentsList.reverse();

    console.log(newCommentsList);

    loadNewCommentsPage(postId, newCommentsList);
}

function loadNewCommentsPage(postId, newCommentsList) {
    // todo
    let numComments;
    let userId = $("body").attr("data-user-id");

    console.log("loadNewCommentsPage");

    if(newCommentsList.length > 0) {

        console.log("length risultato > 0");

        $.each(newCommentsList, function(index, commentJSON) {
            let commentId = commentJSON["id"];
            let readerFullName = commentJSON["reader"]["fullName"];
            let readerId = commentJSON["reader"]["id"];
            let text = commentJSON["text"];
            let timestamp = commentJSON["timestamp"];
            let formattedTimestamp = getFormattedTimestamp(timestamp);

            createNewComment(postId, commentId, readerId, readerFullName, text, timestamp, formattedTimestamp, userId);
        });
        numComments = newCommentsList.length;
    }
    else {
        numComments = 0;
        let page = parseInt(sessionStorage.getItem("commentsPage"));
        sessionStorage.setItem("commentsPage", (page-1).toString());
    }

    nextCommentsPaging(numComments, postId);
}

function nextCommentsPaging(numComments, postId) {
    let postDiv = document.getElementById(postId);
    let showOtherCommentsButton = postDiv.querySelector("button.show-comm");

    if(numComments < COMMENTS_PER_PAGE)
        showOtherCommentsButton.remove();
}