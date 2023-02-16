import {loadScript} from "../util.js";

function initGrid(){
    const zgRef = document.querySelector('zing-grid');
    zgRef.executeOnLoad(() => {
        // zgRef.on('record:click', (event) => console.log(event))
    })
}

loadScript("https://cdn.zinggrid.com/zinggrid.min.js", initGrid)

$(document).ready(() => {
    // const zgRef = $('#users-table')[0];
});
