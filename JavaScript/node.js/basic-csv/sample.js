
var basicCsv = require('basic-csv');

basicCsv.readCSV(process.argv[2], (err, rows) => console.log(rows));
