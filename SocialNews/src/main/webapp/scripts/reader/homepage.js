import {noElemMessage, showMessage} from "../util.js";

const CARDS_PER_PAGE = 10

$(document).ready(async () => {
    sessionStorage.setItem("page","1");
    await loadFollowing();

    $("#previous").on(
        "click",
        () => {
            $("#loading-spinner").removeAttr("style");
            let page = parseInt(sessionStorage.getItem("page"));
            sessionStorage.setItem("page", (page-1).toString());
            loadFollowing();
        }
    );

    $("#next").on(
        "click",
        () => {
            $("#loading-spinner").removeAttr("style");
            let page = parseInt(sessionStorage.getItem("page"));
            sessionStorage.setItem("page", (page+1).toString());
            loadFollowing();
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
        $("#previous-button").removeClass("disabled",false);
}

async function loadFollowing(){
    let page = sessionStorage.getItem("page");
    let reporterList = await $.get(
        `homepage?search=followedCard&page=${page}`
    );
    $("#loading-spinner").attr("style","display:none!important");

    let numCards;
    let reporterListDiv = $("#reporter_list");

    if($(reporterList).find(".card").length > 0) {
        // There are cards inside
        reporterListDiv.empty();
        reporterListDiv.html(reporterList);
        numCards = $(".card-container").length;
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
    else{
        numCards = 0;
        let page = parseInt(sessionStorage.getItem("page"));
        if(page === 1)
            noElemMessage(
                reporterListDiv,
                "You don't follow any reporter. Use the search bar to find and follow new reporters"
            );
        else{
            showMessage('danger', 'There are no other results to show.');
            sessionStorage.setItem("page", (page-1).toString());
        }
    }

    nextPaging(numCards);
    previousPaging();
}