import '../util.js'
import {showMessage} from "../util.js";

$(document).ready(async () => {
    let form = $("#report-form");
    form.on(
        "submit",
        (event) => {
            event.preventDefault();

            if (!event.currentTarget.checkValidity())
                return

            form.addClass('was-validated');

            // Build request body
            let fields = event.currentTarget;
            let formData = {
                reporterId: fields['reporterId'].value,
                postId: fields['postId'].value,
                reason: fields['reason'].value
            };

            $.post(form.action,
                formData,
                function(){
                    showMessage('success', 'Reporter registered successfully');
                    // Clean form content
                    $("#reason").val("");
                })
                .fail(function() {
                    showMessage('danger', `Something went wrong. Please retry later`);
                });
        }
    );
});