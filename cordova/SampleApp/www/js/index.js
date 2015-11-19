
var app = {
    initialize: function() {
        this.bindEvents();
    },
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    onDeviceReady: function() {
        app.receivedEvent('deviceready');
    },
    receivedEvent: function(id) {
        console.log('Received Event: ' + id);

        console.log(navigator.camera);

        navigator.camera.getPicture(
            d => document.getElementById('sample').src = `data:image/jpeg;base64,${d}`,
            e => alert(`failed: ${e}`)
        );
    }
};

app.initialize();