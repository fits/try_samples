
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

        navigator.camera.getPicture(
            function(d) { document.getElementById('sample').src = 'data:image/jpeg;base64,' + d },
            function(e) { el.textContent = 'failed: ' + e },
            { destinationType: Camera.DestinationType.DATA_URL }
        );
    }
};

app.initialize();