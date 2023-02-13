function initGrid(){
    const zgRef = document.querySelector('zing-grid');
    zgRef.executeOnLoad(() => {
        // zgRef.on('record:click', (event) => console.log(event))
    })
}

$.ajax({
    type: "GET",
    url: "https://cdn.zinggrid.com/zinggrid.min.js",
    dataType: "script",
    cache: true,
    success: initGrid
});


$(document).ready(() => {
    // const zgRef = $('#users-table')[0];
});
