"use strict";

// module Sample.Func

const proc = p => () => console.log(p);

exports.procData1 = proc;
exports.procData2 = proc;
exports.procData3 = proc;
exports.procData4 = proc;
exports.procData5 = proc;
exports.procData6 = proc;
exports.procData7 = proc;

const func = f => p => () => console.log( f(p) );

exports.runFunc = func;
exports.runFunc2 = func;
