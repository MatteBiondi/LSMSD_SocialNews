// *********** Constant section ***********
const DEFAULT_SEARCH = "Reporter Name";
// ****************************************

// Code on document ready
$(document).ready(() => {
    // Get search element of the page
    let search_items = $(".search-item");
    let search_text = $("#search-text");
    let search_clear = $("#search-clear");

    search_text.attr("placeholder",`${DEFAULT_SEARCH}:`)


    // Define event handlers
    search_items.on(
        "click",
        (elem) => {
            search_text.attr("placeholder", `${elem.currentTarget.innerText}:`);
        });

    search_clear.on(
        "click",
        () => {
            search_text.val("");
            search_clear.hide();
        }
    );

    search_text.on(
        "input",
        () => {
            search_clear.css("display", search_text.val() !== "" ? "inherit":"none");
        });
})