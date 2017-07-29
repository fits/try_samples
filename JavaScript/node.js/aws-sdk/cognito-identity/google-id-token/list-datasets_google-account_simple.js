
const AWS = require('aws-sdk');

const identityPoolId = process.argv[2];
const idToken = process.argv[3];

AWS.config.region = 'ap-northeast-1';

AWS.config.credentials = new AWS.CognitoIdentityCredentials({
	IdentityPoolId: identityPoolId,
	Logins: {
		'accounts.google.com': idToken
	}
});

AWS.config.credentials.refresh((err) => {
	if (err) {
		console.error(err);
		return;
	}

	console.log(AWS.config.credentials);

	const cognitoSync = new AWS.CognitoSync();

	const params = {
		IdentityId: AWS.config.credentials.params.IdentityId,
		IdentityPoolId: AWS.config.credentials.params.IdentityPoolId
	};

	cognitoSync.listDatasets(params)
		.promise()
		.then(r => {
			console.log(r);
		})
		.catch(console.error);

});

