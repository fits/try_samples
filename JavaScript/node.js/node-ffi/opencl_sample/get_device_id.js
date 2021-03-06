
'use strict';

const ffi = require('ffi');
const ref = require('ref');

const CL_DEVICE_TYPE_DEFAULT = 1;

const uintPtr = ref.refType(ref.types.uint32);
const sizeTPtr = ref.refType('size_t');

const clLib = (process.platform == 'win32') ? 'OpenCL' : 'libOpenCL';

const openCl = ffi.Library(clLib, {
	'clGetPlatformIDs': ['int', ['uint', sizeTPtr, uintPtr]],
	'clGetDeviceIDs': ['int', ['size_t', 'ulong', 'uint', sizeTPtr, uintPtr]]
});

const checkError = (errCode, title = '') => {
	if (errCode != 0) {
		throw new Error(`${title} Error: ${errCode}`);
	}
};

const platformIdsPtr = ref.alloc(sizeTPtr);

let res = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

checkError(res, 'clGetPlatformIDs');

const platformId = sizeTPtr.get(platformIdsPtr);

console.log(`platformId: ${platformId}`);

const deviceIdsPtr = ref.alloc(sizeTPtr);

res = openCl.clGetDeviceIDs(platformId, CL_DEVICE_TYPE_DEFAULT, 1, deviceIdsPtr, null);

checkError(res, 'clGetDeviceIDs');

const deviceId = sizeTPtr.get(deviceIdsPtr);

console.log(`deviceId: ${deviceId}`);
