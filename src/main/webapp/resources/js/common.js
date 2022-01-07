function notificationPopUp() {

    var popup = document.getElementById("myPopup");
    document.getElementById('myPopup').contentWindow.location.reload(true);

    popup.focus();
    popup.classList.toggle("show");
    resetCommand();
    document.getElementById('nots').innerHTML = 0;

}

function socketListener(message, channel, event) {
    console.log(message);

    document.getElementById('nots').innerHTML = message;


}


