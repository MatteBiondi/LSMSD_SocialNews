import {noElemMessage, showMessage} from "../util.js";
import {showComments, publishNewComment} from "../reporter/homepage.js"

const CARDS_PER_PAGE = 25

$(document).ready(async () => {
    sessionStorage.setItem("page","1");
    // Remove fields of previous search, if exist
    sessionStorage.setItem("lastId", "");
    sessionStorage.setItem("lastValue", "");

    await loadResults();

    $("#previous").on(
        "click",
        () => {
            $("#loading-spinner").removeAttr("style");
            let page = parseInt(sessionStorage.getItem("page"));
            sessionStorage.setItem("page", (page-1).toString());

            // Considering the direction of page surfing, last is the post at the beginning of the page
            takeLastPost("previous");

            loadResults("previous");
        }
    );

    $("#next").on(
        "click",
        () => {
            $("#loading-spinner").removeAttr("style");
            let page = parseInt(sessionStorage.getItem("page"));
            sessionStorage.setItem("page", (page+1).toString());

            // Considering the direction of page surfing, last is the post at the end of the page
            takeLastPost("next");

            loadResults("next");
        }
    );
});

function nextPaging(numCards) {
    if(numCards < CARDS_PER_PAGE)
        $("#next-button").addClass("disabled");
    else
        $("#next-button").removeClass("disabled");
}

function previousPaging() {
    let page = parseInt(sessionStorage.getItem("page"));
    if( page < 2 )
        $("#previous-button").addClass("disabled");
    else
        $("#previous-button").removeClass("disabled");
}

function takeLastPost(direction){
    let lastResult;
    let searchKey = sessionStorage.getItem("searchKey");

    if(direction === "previous")
        lastResult = $(".search-result").first();
    else
        lastResult = $(".search-result").last();

    sessionStorage.setItem("lastId", lastResult.attr('id'));

    let lastValue = searchKey === "Hashtag"?
        lastResult.attr("data-millis-time"):lastResult.attr("data-full-name");

    sessionStorage.setItem("lastValue", lastValue);
}

async function loadResults(direction){
    let searchKey = sessionStorage.getItem("searchKey");
    let searchValue = sessionStorage.getItem("searchValue");
    let lastId = sessionStorage.getItem("lastId");
    let lastValue = sessionStorage.getItem("lastValue");
    let page = parseInt(sessionStorage.getItem("page"));

    //Last value is timestamp for post and fullName for reporter
    let resultList = await $.get(
        `search?by=${searchKey}&value=${searchValue}&page=${page}&lastId=${lastId}&lastValue=${lastValue}&direction=${direction}`
    );
    $("#loading-spinner").attr("style","display:none!important");

    let numCards;
    let resultListDiv = $("#result_list");

    if($(resultList).find(".search-result").length > 0) {
        // There are cards inside
        resultListDiv.empty();
        resultListDiv.append(resultList);
        numCards = $(".search-result").length;

        await applyButtonsEventListener(resultList);
    }
    else{
        numCards = 0;
        let page = parseInt(sessionStorage.getItem("page"));
        if(page === 1)
            noElemMessage(
                resultListDiv,
                "Your search produced no results. Try again."
            );
        else{
            showMessage('danger', 'There are no other results to show.');
            sessionStorage.setItem("page", (page-1).toString());
        }
    }

    nextPaging(numCards);
    previousPaging();
}

async function applyButtonsEventListener() {
    const showButtons = document.querySelectorAll(".show-comm");

    for (const button of showButtons) {
        button.addEventListener("click", function () {
            const postId = this.getAttribute("data-post");
            const reporter = this.getAttribute("data-reporter");
            showComments(reporter, postId);
        });
    }

    const publishButtons = document.querySelectorAll(".write-comment");

    for (const button of publishButtons) {
        button.addEventListener("click", function () {
            const postId = this.closest('.post-container').id;
            const reporterId = this.getAttribute("data-reporter");
            publishNewComment(postId, reporterId);
        });
    }
}