
const {
    AuthenticationDetails, CognitoUserPool, CognitoUser
} = require('amazon-cognito-identity-js')

const { GraphQLClient, gql } = require('graphql-request')

const endpoint = 'https://xxxxxx.appsync-api.xxxx.amazonaws.com/graphql'
const userPoolId = 'xxxx_xxxxx'
const clientId = 'xxxxxx'

const userName = process.argv[2]
const password = process.argv[3]

const query = gql`
    {
        item {
            id
        }
    }
`

const userPool = new CognitoUserPool({
    UserPoolId: userPoolId,
    ClientId: clientId
})

const user = new CognitoUser({
    Username: userName,
    Pool: userPool
})

const ath = new AuthenticationDetails({
    Username: userName,
    Password: password
})

const handler = {
    onSuccess: res => {
        console.log('*** success')

        const token = res.idToken.jwtToken
        //const token = res.accessToken.jwtToken

        const graphqlClient = new GraphQLClient(endpoint, {
            headers: {
                authorization: token
            }
        })

        graphqlClient.request(query)
            .then(d => console.log(d))
            .catch(err => console.error(err))
    },
    onFailure: err => console.error(err),
    newPasswordRequired: (uattr) => {
        console.log('*** new password')
        user.completeNewPasswordChallenge(password, uattr, handler)
    }
}

user.authenticateUser(ath, handler)
