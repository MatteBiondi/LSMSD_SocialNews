$(document).ready(function(){
    $("input[type='checkbox']").click(function(){
        if($(this).prop("checked") === true){
            $("input[type='checkbox']").not(this).prop("disabled", true);
        }
        else if($(this).prop("checked") === false){
            $("input[type='checkbox']").not(this).prop("disabled", false);
        }
    });
});