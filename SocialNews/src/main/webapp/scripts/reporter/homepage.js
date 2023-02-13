function publishNewPost(reporterID) {
    console.log(reporterID);
    let textarea = $("textarea").val();
    let hashtags = $("#hashtags-input").val();
    let links = $("#related-links-input").val();
    $.post(
        "publishNewPost.jsp",
        {
            reporterID: reporterID,
            text: textarea,
            hashtags: hashtags,
            links: links
        },
        function(postID) {
            //todo
        }
    );
}
