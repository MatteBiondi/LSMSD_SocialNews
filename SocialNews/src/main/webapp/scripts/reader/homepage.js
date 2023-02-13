const CARDS_PER_PAGE = 25

$(document).ready(async () => {
    sessionStorage.setItem("page","1");
    await loadFollowing();

    $("#previous").on(
        "click",
        () => {
            let page = parseInt(sessionStorage.getItem("page"));
            sessionStorage.setItem("page", (page-1).toString());
            loadFollowing();
        }
    );

    $("#next").on(
        "click",
        () => {
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

    let numCards;

    if($(reporterList).find(".card").length > 0) {
        // There are cards inside
        let reporterListDiv = $("#reporter_list");
        reporterListDiv.empty();
        reporterListDiv.html(reporterList);
        numCards = $(".card-container").length;
    }
    else{
        numCards = 0;
        let page = parseInt(sessionStorage.getItem("page"));
        sessionStorage.setItem("page", (page-1).toString());
    }

    nextPaging(numCards);
    previousPaging();
}