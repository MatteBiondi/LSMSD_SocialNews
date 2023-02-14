// *********** Constant section ***********
const DEFAULT_SEARCH = "Reporter Name";
const EMPTY_FIELD_MESSAGE = "The search value is mandatory";
// ****************************************

// Code on document ready
$(document).ready(() => {
    // Get search element of the page
    let search_items = $(".search-item");
    let search_text = $("#search-text");
    let search_clear = $("#search-clear");
    let search_button = $("#search-button");

    let searchKey = sessionStorage.getItem("searchKey");
    searchKey = searchKey === null? DEFAULT_SEARCH : searchKey;
    search_text.attr("placeholder",`${searchKey}:`);
    sessionStorage.setItem("searchKey", searchKey);


    // Define event handlers
    search_items.on(
        "click",
        (elem) => {
            let searchByText = elem.currentTarget.innerText;
            search_text.attr("placeholder", `${searchByText}:`);
            sessionStorage.setItem("searchKey", searchByText);
        });

    search_clear.on(
        "click",
        () => {
            search_text.val("");
            search_clear.css("visibility", "hidden");
        }
    );

    search_text.on(
        "input",
        () => {
            search_clear.css("visibility", search_text.val() !== "" ? "inherit":"hidden");
        });

    search_text.on(
        "keypress",
        (e) => {
            // If the user presses the "Enter" key on the keyboard
            if (e.key === "Enter") {
                // Cancel the default action
                e.preventDefault();
                // Trigger the button element with a click
                search_button.click();
            }
        });

    search_button.on(
        "click",
        () => {
            let searchKey = sessionStorage.getItem("searchKey");
            let searchValue = search_text.val();

            if(searchValue === ""){
                alert(EMPTY_FIELD_MESSAGE);
                return;
            }
            sessionStorage.setItem("searchValue", searchValue);

            window.location.href=`search?by=${searchKey}&value=${searchValue}`
        }
    );
})