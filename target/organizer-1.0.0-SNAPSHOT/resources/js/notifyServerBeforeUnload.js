window.notifyServerBeforeUnload = function(element){
    window.addEventListener('beforeunload', function (e) {
        element.$server.beforeUnload();
    })
}