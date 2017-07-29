
const jwtDecode = require('jwt-decode');

console.log(jwtDecode(process.argv[2]));
