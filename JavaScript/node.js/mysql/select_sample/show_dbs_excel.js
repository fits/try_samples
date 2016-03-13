"use strict";

const mysql = require('mysql');
const Excel = require('exceljs');

const con = mysql.createConnection({
	user: 'root'
});

con.connect();

const sql = 'show databases';

con.query(sql, (err, rs, fs) => {
	if (!err) {
		const wb = new Excel.Workbook();
		const sheet = wb.addWorksheet('result');

		sheet.columns = fs.map( f => 
			new Object({ header: f.name, key: f.name }));

		rs.forEach( r => sheet.addRow(r) );

		wb.xlsx.writeFile('result.xlsx')
			.then( () => console.log('created') );
	}
});

con.end();
