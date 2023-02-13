$(document).ready(async () => {
    let reporterList = await $.get(
        `statistics?search=suggestedCard`
    );

    $("#reporter_list").html(reporterList);
});