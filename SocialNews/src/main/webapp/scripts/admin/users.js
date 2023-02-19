import {loadScript, computeWindowHeight, showMessage} from "../util.js";

const script = loadScript("https://cdn.zinggrid.com/zinggrid.min.js", initGrid)

$(document).ready(async () => {
    try {
        await script;
    }
    catch (error){
        showMessage('danger', 'Failed to load resources');
    }
});

function initGrid(){
    const zgRef = $('zing-grid');
    zgRef[0].executeOnLoad(() => {
        zgRef[0].setHeight(`${
            computeWindowHeight()
            * (parseInt($('body').css('--content-height'))/100)
            - $('zg-footer').height()
            - $('zg-caption').height()
            - $('zg-row').height()}px`
        );
        addEventListeners();
        zgRef.on('data:load', addEventListeners)
    })
}

function showReporterHomepage(ev){
    location.href = `${document.URL.slice(0,document.URL.search('admin')-1)}/reporter?reporterId=${ev.currentTarget.dataset['id']}`
}

function addEventListeners(){
    $('.view-btn').on("click", showReporterHomepage);
    $('zg-button').removeAttr('tooltip');
}
