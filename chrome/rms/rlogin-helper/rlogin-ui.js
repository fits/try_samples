
/** 
 * form のサブミット処理
 *
 * input[name=submit] により document.forms[0].submit() が使えない問題の
 * 回避措置
 */
var submitForm = function() {
	document.forms[0].submit.click();
};

var tasks = {
	'BizAuthCustomerAttest': function(params) {
		document.getElementsByName('login_id')[0].value = params['rloginId'];
		document.getElementsByName('passwd')[0].value = params['rloginPassword'];
		submitForm();
	},
	'BizAuthUserAttest': function(params) {
		document.getElementsByName('user_id')[0].value = params['userId'];
		document.getElementsByName('user_passwd')[0].value = params['userPassword'];
		submitForm();
	},
	'BizAuthAnnounce': function() {
		submitForm();
	}
};

chrome.runtime.onMessage.addListener(function(req, sender, callback) {
	var action = document.getElementsByName('action')[0].value;
	var task = tasks[action];

	if (task) {
		task(req);
	}
	else {
		action = 'end';
	}

	callback(action);
});
