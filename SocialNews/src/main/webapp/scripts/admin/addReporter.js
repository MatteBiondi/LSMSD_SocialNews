import "../util.js"
import {showMessage} from "../util.js";

$(document).ready(() => {
    $('#country').countrypicker();
    $('#dob')[0].max = new Date().toISOString().split("T")[0];

    const form = $('#reporter-form');
    form.on('submit', event => submitForm(form, event));
});

async function submitForm(form, event) {
    event.preventDefault();
    event.stopPropagation();
    form.addClass('was-validated');

    if (!event.currentTarget.checkValidity())
        return

    // Build request body
    let fields = event.currentTarget;

    let reporter = {
        email: fields['email'].value,
        password: fields['password'].value,
        fullName: `${fields['firstName'].value.capitalize()} ${fields['lastName'].value.capitalize()}`,
        gender: fields['gender'].value
    };

    let location = fields['addressStreet'].value.capitalize();

    if (location !== '' && fields['addressNumber'].value !== '')
        location = `${location}, ${fields['addressNumber'].value}`

    if (location !== '' && fields['city'].value !== '')
        location = `${location} - ${fields['city'].value.capitalize()}`
    else if (fields['city'].value !== '')
        location = fields['city'].value

    if (location !== '' && fields['zip'].value !== '')
        location = `${location}, ${fields['zip'].value}`

    if (location !== '')
        location = `${location} ${fields['country'].value}`
    else
        location = fields['country'].value

    reporter['location'] = location;

    if (fields['dateOfBirthday'] !== '')
        reporter['dateOfBirth'] = fields['dateOfBirthday'].value;

    if (fields['cell'] !== '')
        reporter['cell'] = fields['cell'].value;

    if (fields['picture'].files.length > 0) {
        reporter['picture'] = await new Promise((resolve) => {
            let fileReader = new FileReader();
            fileReader.onload = () => resolve(fileReader.result);
            fileReader.readAsDataURL(fields['picture'].files[0]);
        });
    }

    // Send request
    let response = await $.ajax(
        document.URL,
        {
            method: 'post',
            contentType: 'application/json',
            data: JSON.stringify(reporter)
        });

    // Show result
    if(response['result'] === 'success'){
        showMessage('success', 'Reporter registered successfully')
        form.removeClass('was-validated');
        form[0].reset()
    }
    else {
        showMessage('danger', `Something went wrong: ${response['message'].toLowerCase()}`)
    }
}