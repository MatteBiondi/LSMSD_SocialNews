Object.defineProperty(String.prototype, 'capitalize', {
    value: function() {
        return  this.split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(' ');
    },
    enumerable: false
});

function showMessage(type, text){
    $("#message").html(`
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <div>${text}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`
    );

    let alert_elem = $('.alert');
    setTimeout(() => {alert_elem.alert("close"); alert_elem.parent().remove(alert_elem)}, 2000);
}