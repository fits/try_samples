
'use strict';

const ref = require('ref');
const ffi = require('ffi');

const CL_PLATFORM_PROFILE = 0x0900;
const CL_PLATFORM_VERSION = 0x0901;
const CL_PLATFORM_NAME = 0x0902;
const CL_PLATFORM_VENDOR = 0x0903;
const CL_PLATFORM_EXTENSIONS = 0x0904;
const CL_PLATFORM_HOST_TIMER_RESOLUTION = 0x0905;

const uintPtr = ref.refType(ref.types.uint32);
const uintPtrPtr = ref.refType(uintPtr);

const openCl = ffi.Library('OpenCL', {
	'clGetPlatformIDs': ['int', ['uint', uintPtrPtr, uintPtr]],
	'clGetPlatformInfo': ['int', ['uint', 'int', 'uint', 'pointer', uintPtr]]
});

const printPlatformInfo = (platformId, paramName) => {
	let sizePtr = ref.alloc(uintPtr);

	let res = openCl.clGetPlatformInfo(pid, paramName, 0, null, sizePtr);

	if (res == 0) {
		let size = uintPtr.get(sizePtr);
		let buf = Buffer.alloc(size);

		openCl.clGetPlatformInfo(pid, paramName, size, buf, null);

		console.log(buf.toString());
	}
};

let platformIdsPtr = ref.alloc(uintPtrPtr);

let res = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

if (res != 0) {
	throw 'ERROR: ' + res;
}

let pid = uintPtrPtr.get(platformIdsPtr);

[
	CL_PLATFORM_PROFILE,
	CL_PLATFORM_VERSION,
	CL_PLATFORM_NAME,
	CL_PLATFORM_VENDOR,
	CL_PLATFORM_EXTENSIONS
].forEach( p => 
	printPlatformInfo(pid, p)
);
