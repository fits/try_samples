"use strict";

const mysql = require('mysql');

const con = mysql.createConnection({
	user: 'root'
});

con.connect();

con.query('show databases', (err, rs, fs) => {
	if (err) throw err;

	console.log(rs);
	console.log(fs);
});

con.end();
