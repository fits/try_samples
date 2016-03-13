
const Excel = require('exceljs');

const wb = new Excel.Workbook();

const sheet = wb.addWorksheet('シート1');

sheet.columns = [
	{header: 'ID', key: 'id'},
	{header: 'NAME', key: 'name'}
];

sheet.addRow({id: 1, name: 'アイテム1'});
sheet.addRow({id: 2, name: 'アイテム2'});

sheet.addRow([3, "aaa"]);
sheet.addRow([4, "データ1"]);

wb.xlsx.writeFile('sample.xlsx')
	.then( () => console.log('created') );
