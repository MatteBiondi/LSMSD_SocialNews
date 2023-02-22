$(document).ready(async () => {
    let reporterList = await $.get(
        `statistics?search=suggestedCard`
    );

    $("#loading-spinner").attr("style","display:none!important");

    $("#reporter_list").html(reporterList);
});