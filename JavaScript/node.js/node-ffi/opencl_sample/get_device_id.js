
'use strict';

const ref = require('ref');
const ffi = require('ffi');

const CL_DEVICE_TYPE_DEFAULT = 1;

const uintPtr = ref.refType(ref.types.uint32);
const uintPtrPtr = ref.refType(uintPtr);

const openCl = ffi.Library('OpenCL', {
	'clGetPlatformIDs': ['int', ['uint', uintPtrPtr, uintPtr]],
	'clGetDeviceIDs': ['int', ['uint', 'int', 'uint', uintPtrPtr, uintPtr]]
});

let platformIdsPtr = ref.alloc(uintPtrPtr);

let res1 = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

if (res1 != 0) {
	throw 'ERROR: ' + res1;
}

console.log(platformIdsPtr);

let platformId = uintPtrPtr.get(platformIdsPtr);

console.log('platformId: ' + platformId);

let deviceIdsPtr = ref.alloc(uintPtrPtr);

let res2 = openCl.clGetDeviceIDs(platformId, CL_DEVICE_TYPE_DEFAULT, 1, deviceIdsPtr, null);

if (res2 != 0) {
	throw 'ERROR: ' + res2;
}
console.log(deviceIdsPtr);

let deviceId = uintPtrPtr.get(deviceIdsPtr);

console.log('deviceId: ' + deviceId);

