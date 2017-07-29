
const AWS = require('aws-sdk');

AWS.config.region = 'ap-northeast-1';

const cognitoIdentity = new AWS.CognitoIdentity();

const identityPoolId = process.argv[2];
const idToken = process.argv[3];

const params = {
	IdentityPoolId: identityPoolId,
	Logins: {
		'accounts.google.com': idToken
	}
};

cognitoIdentity.getId(params).promise()
	.then(res => {
		const cparams = {
			IdentityId: res.IdentityId,
			Logins: {
				'accounts.google.com': idToken
			}
		};

		return cognitoIdentity.getCredentialsForIdentity(cparams).promise()
	})
	.then(res => {

		const cognitoSync = new AWS.CognitoSync({
			credentials: {
				accessKeyId: res.Credentials.AccessKeyId,
				secretAccessKey: res.Credentials.SecretKey,
				sessionToken: res.Credentials.SessionToken
			}
		});

		const sparams = {
			IdentityId: res.IdentityId,
			IdentityPoolId: identityPoolId
		};

		return cognitoSync.listDatasets(sparams).promise();
	})
	.then(console.log)
	.catch(console.error);
