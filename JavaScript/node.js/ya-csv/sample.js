
const csv = require('ya-csv');

const reader = csv.createCsvFileReader(process.argv[2]);

const data = [];

reader.addListener('data', d => data.push(d));
reader.addListener('end', () => console.log(data));
