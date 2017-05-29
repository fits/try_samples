
const AmazonCognitoIdentity = require('amazon-cognito-identity-js');
const fetch = require('node-fetch');

const poolId = process.argv[2];
const clientId = process.argv[3];

const user = process.argv[4];
const password = process.argv[5];

const apiUrl = process.argv[6];

const userPool = new AmazonCognitoIdentity.CognitoUserPool({
	UserPoolId: poolId,
	ClientId: clientId
});

const cognitoUser = new AmazonCognitoIdentity.CognitoUser({
	Username: user,
	Pool: userPool
});

const authParam = new AmazonCognitoIdentity.AuthenticationDetails({
	Username: user,
	Password: password
});

const handler = {
	onSuccess: res => {
		console.log('- onSuccess');

		// call API Gateway
		fetch(apiUrl, { headers: { Authorization: res.idToken.jwtToken } })
			.then(console.log)
			.catch(console.error);
	},
	newPasswordRequired: (uattrs, rattrs) => {
		console.log('- newPasswordRequired');

		cognitoUser.completeNewPasswordChallenge(password, uattrs, handler);
	},
	onFailure: console.error
};

cognitoUser.authenticateUser(authParam, handler);
