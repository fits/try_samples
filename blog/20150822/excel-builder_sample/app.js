
require(['excel-builder'], function(EB) {
	var wb = EB.createWorkbook();
	var sh = wb.createWorksheet();

	sh.setData([
		['aaa', 10],
		['サンプル', 2],
		['てすと', 3],
		['計', {value: 'sum(B1:B3)', metadata: {type: 'formula'}}]
	]);

	wb.addWorksheet(sh);

	var trg = document.getElementById("dw");

	trg.href = 'data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,' + EB.createFile(wb);

	trg.download = 'sample.xlsx';
});
