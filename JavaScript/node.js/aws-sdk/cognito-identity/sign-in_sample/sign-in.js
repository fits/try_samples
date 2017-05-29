
const AmazonCognitoIdentity = require('amazon-cognito-identity-js');

const poolId = process.argv[2];
const clientId = process.argv[3];

const user = process.argv[4];
const password = process.argv[5];
const newPassword = process.argv[6];

const userPool = new AmazonCognitoIdentity.CognitoUserPool({
	UserPoolId: poolId,
	ClientId: clientId
})

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
		console.log(res);
	},
	newPasswordRequired: (uattrs, rattrs) => {
		console.log('- newPasswordRequired');
		cognitoUser.completeNewPasswordChallenge(newPassword, uattrs, handler);
	},
	onFailure: console.error
};

cognitoUser.authenticateUser(authParam, handler);
