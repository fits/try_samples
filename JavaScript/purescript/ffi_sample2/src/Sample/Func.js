"use strict";

// module Sample.Func

const proc = p => () => console.log(p);

exports.procData1 = proc;
exports.procData2 = proc;
exports.procData3 = proc;
exports.procData4 = proc;

exports.runFunc = f => p => () => console.log( f(p) );
