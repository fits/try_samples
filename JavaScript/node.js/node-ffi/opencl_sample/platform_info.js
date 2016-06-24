
'use strict';

const ffi = require('ffi');
const ref = require('ref');

const CL_PLATFORM_PROFILE = 0x0900;
const CL_PLATFORM_VERSION = 0x0901;
const CL_PLATFORM_NAME = 0x0902;
const CL_PLATFORM_VENDOR = 0x0903;
const CL_PLATFORM_EXTENSIONS = 0x0904;
const CL_PLATFORM_HOST_TIMER_RESOLUTION = 0x0905;

const uintPtr = ref.refType(ref.types.uint32);
const uintPtrPtr = ref.refType(uintPtr);
const sizeTPtr = ref.refType('size_t');

const openCl = ffi.Library('OpenCL', {
	'clGetPlatformIDs': ['int', ['uint', sizeTPtr, uintPtr]],
	'clGetPlatformInfo': ['int', ['size_t', 'uint', 'size_t', 'pointer', sizeTPtr]]
});

const checkError = (errCode, title = '') => {
	if (errCode != 0) {
		throw new Error(`${title} Error: ${errCode}`);
	}
};

const printPlatformInfo = (pid, paramName) => {
	const sPtr = ref.alloc(sizeTPtr);

	let res = openCl.clGetPlatformInfo(pid, paramName, 0, null, sPtr);

	checkError(res, 'clGetPlatformInfo1');

	const size = sizeTPtr.get(sPtr);
	const buf = Buffer.alloc(size);

	res = openCl.clGetPlatformInfo(pid, paramName, size, buf, null);

	checkError(res, 'clGetPlatformInfo2');

	console.log(buf.toString());
};

const platformIdsPtr = ref.alloc(sizeTPtr);

const res = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

checkError(res, 'clGetPlatformIDs');

const platformId = sizeTPtr.get(platformIdsPtr);

[
	CL_PLATFORM_PROFILE,
	CL_PLATFORM_VERSION,
	CL_PLATFORM_NAME,
	CL_PLATFORM_VENDOR,
	CL_PLATFORM_EXTENSIONS
].forEach( p => 
	printPlatformInfo(platformId, p)
);
