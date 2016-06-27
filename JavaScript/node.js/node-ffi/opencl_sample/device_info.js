
'use strict';

const ffi = require('ffi');
const ref = require('ref');

const CL_DEVICE_TYPE_DEFAULT = (1 << 0);
const CL_DEVICE_TYPE_CPU = (1 << 1);
const CL_DEVICE_TYPE_GPU = (1 << 2);
const CL_DEVICE_TYPE_ACCELERATOR = (1 << 3);
const CL_DEVICE_TYPE_CUSTOM = (1 << 4);
const CL_DEVICE_TYPE_ALL = 0xFFFFFFFF;

const CL_DEVICE_NAME = 0x102B;
const CL_DEVICE_PROFILE = 0x102E;

const uintPtr = ref.refType(ref.types.uint32);
const sizeTPtr = ref.refType('size_t');

const openCl = ffi.Library('OpenCL', {
	'clGetPlatformIDs': ['int', ['uint', sizeTPtr, uintPtr]],
	'clGetDeviceIDs': ['int', ['size_t', 'ulong', 'uint', sizeTPtr, uintPtr]],
	'clGetDeviceInfo': ['int', ['size_t', 'uint', 'size_t', 'pointer', sizeTPtr]]
});

const checkError = (errCode, title = '') => {
	if (errCode != 0) {
		throw new Error(`${title} Error: ${errCode}`);
	}
};

const printDeviceInfo = (did, paramName) => {
	const sPtr = ref.alloc(sizeTPtr);

	let res = openCl.clGetDeviceInfo(did, paramName, 0, null, sPtr);

	checkError(res, 'clGetDeviceInfo size');

	const size = sizeTPtr.get(sPtr);
	const buf = Buffer.alloc(size);

	res = openCl.clGetDeviceInfo(did, paramName, size, buf, null);

	checkError(res, 'clGetDeviceInfo data');

	console.log(buf.toString());
};


const platformIdsPtr = ref.alloc(sizeTPtr);

let res = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

checkError(res, 'clGetPlatformIDs');

const platformId = sizeTPtr.get(platformIdsPtr);

console.log(`platformId: ${platformId}`);

[
	CL_DEVICE_TYPE_DEFAULT,
	CL_DEVICE_TYPE_CPU,
	CL_DEVICE_TYPE_GPU,
	CL_DEVICE_TYPE_ACCELERATOR,
	CL_DEVICE_TYPE_CUSTOM,
	CL_DEVICE_TYPE_ALL
].forEach( d => {
	console.log(`----- ${d} -----`);

	const deviceIdsPtr = ref.alloc(sizeTPtr);

	res = openCl.clGetDeviceIDs(platformId, d, 1, deviceIdsPtr, null);

	if (res == 0) {
		const deviceId = sizeTPtr.get(deviceIdsPtr);

		[
			CL_DEVICE_NAME,
			CL_DEVICE_PROFILE
		].forEach( p => printDeviceInfo(deviceId, p) );
	}
});

