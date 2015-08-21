
require(['excel-builder'], function(EB) {
	var wb = EB.createWorkbook();
	var sh = wb.createWorksheet();

	sh.setData([
		['test1', 100, true],
		['サンプル', 2, false],
		['てすとa', 3, false],
		['計', {value: 'sum(B1:B3)', metadata: {type: 'formula'}}]
	]);

	wb.addWorksheet(sh);

	var trg = document.getElementById("dlink");

	trg.href = 'data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,' + EB.createFile(wb);

	trg.download = 'sample.xlsx';
});
