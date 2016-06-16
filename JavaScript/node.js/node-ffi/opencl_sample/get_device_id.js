
'use strict';

const ref = require('ref');
const ffi = require('ffi');

const CL_DEVICE_TYPE_DEFAULT = 1;

const intPtr = ref.refType(ref.types.int);
const intPtrPtr = ref.refType(intPtr);

const openCl = ffi.Library('OpenCL', {
	'clGetPlatformIDs': ['int', ['int', intPtrPtr, intPtr]],
	'clGetDeviceIDs': ['int', ['int', 'int', 'int', intPtrPtr, intPtr]]
});

let platformIdsPtr = ref.alloc(intPtrPtr);

let res1 = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

if (res1 != 0) {
	throw 'ERROR: ' + res1;
}

console.log(platformIdsPtr);

let platformId = platformIdsPtr.readInt32LE();

console.log('platformId: ' + platformId);

let deviceIdsPtr = ref.alloc(intPtrPtr);

let res2 = openCl.clGetDeviceIDs(platformId, CL_DEVICE_TYPE_DEFAULT, 1, deviceIdsPtr, null);

if (res2 != 0) {
	throw 'ERROR: ' + res2;
}
console.log(deviceIdsPtr);

let deviceId = deviceIdsPtr.readInt32LE();

console.log('deviceId: ' + deviceId);

