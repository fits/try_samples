var exec = require('cordova/exec');

exports.showMessage = function(arg, success, error) {
    exec(success, error, 'EchoPlugin', 'showMessage', [arg]);
};
