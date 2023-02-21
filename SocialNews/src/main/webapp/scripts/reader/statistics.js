$(document).ready(async () => {
    let reporterList = await $.get(
        `statistics?search=suggestedCard`
    );

    $("#loading-spinner").remove();

    $("#reporter_list").html(reporterList);
});