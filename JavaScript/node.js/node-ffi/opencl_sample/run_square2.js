
'use strict';

const fs = require('fs');
const ref = require('ref');
const ArrayType = require('ref-array');
const ffi = require('ffi');

const CL_DEVICE_TYPE_DEFAULT = 1;

const CL_MEM_READ_WRITE = (1 << 0);
const CL_MEM_WRITE_ONLY = (1 << 1);
const CL_MEM_READ_ONLY = (1 << 2);
const CL_MEM_USE_HOST_PTR = (1 << 3);
const CL_MEM_ALLOC_HOST_PTR = (1 << 4);
const CL_MEM_COPY_HOST_PTR = (1 << 5);

const intPtr = ref.refType(ref.types.int32);
const uintPtr = ref.refType(ref.types.uint32);
const sizeTPtr = ref.refType('size_t');
const StringArray = ArrayType('string');

const openCl = ffi.Library('OpenCL', {
	'clGetPlatformIDs': ['int', ['uint', sizeTPtr, uintPtr]],
	'clGetDeviceIDs': ['int', ['size_t', 'ulong', 'uint', sizeTPtr, uintPtr]],
	'clCreateContext': ['pointer', ['pointer', 'uint', sizeTPtr, 'pointer', 'pointer', intPtr]],
	'clReleaseContext': ['int', ['pointer']],
	'clCreateProgramWithSource': ['pointer', ['pointer', 'uint', StringArray, sizeTPtr, intPtr]],
	'clBuildProgram': ['int', ['pointer', 'uint', sizeTPtr, 'string', 'pointer', 'pointer']],
	'clReleaseProgram': ['int', ['pointer']],
	'clCreateKernel': ['pointer', ['pointer', 'string', intPtr]],
	'clReleaseKernel': ['int', ['pointer']],
	'clCreateBuffer': ['pointer', ['pointer', 'ulong', 'size_t', 'pointer', intPtr]],
	'clReleaseMemObject': ['int', ['pointer']],
	'clSetKernelArg': ['int', ['pointer', 'uint', 'size_t', 'pointer']],
	'clCreateCommandQueue': ['pointer', ['pointer', 'size_t', 'ulong', intPtr]],
	'clReleaseCommandQueue': ['int', ['pointer']],
	'clEnqueueReadBuffer': ['int', ['pointer', 'pointer', 'bool', 'size_t', 'size_t', 'pointer', 'uint', 'pointer', 'pointer']],
	'clEnqueueNDRangeKernel': ['int', ['pointer', 'pointer', 'uint', sizeTPtr, sizeTPtr, sizeTPtr, 'uint', 'pointer', 'pointer']]
});

const checkError = (err, title = '') => {
	if (err instanceof Buffer) {
		err = intPtr.get(err);
	}

	if (err != 0) {
		throw new Error(`${title} Error: ${err}`);
	}
};

const dataTypeSize = 4;
const data = [1.1, 2.2, 3.3];

const code = fs.readFileSync(process.argv[2]);

const releaseList = [];

try {
	let platformIdsPtr = ref.alloc(sizeTPtr);

	let res = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

	checkError(res, 'clGetPlatformIDs');

	let platformId = sizeTPtr.get(platformIdsPtr);

	let deviceIdsPtr = ref.alloc(sizeTPtr);

	res = openCl.clGetDeviceIDs(platformId, CL_DEVICE_TYPE_DEFAULT, 1, deviceIdsPtr, null);

	checkError(res, 'clGetDeviceIDs');

	let deviceId = sizeTPtr.get(deviceIdsPtr);

	let errPtr = ref.alloc(intPtr);

	let ctx = openCl.clCreateContext(null, 1, deviceIdsPtr, null, null, errPtr)

	checkError(errPtr, 'clCreateContext');
	releaseList.push( () => openCl.clReleaseContext(ctx) );

	let queue = openCl.clCreateCommandQueue(ctx, deviceId, 0, errPtr);

	checkError(errPtr, 'clCreateCommandQueue');
	releaseList.push( () => openCl.clReleaseCommandQueue(queue) );

	let codeArray = new StringArray([code.toString()]);

	let program = openCl.clCreateProgramWithSource(ctx, 1, codeArray, null, errPtr);

	checkError(errPtr, 'clCreateProgramWithSource');
	releaseList.push( () => openCl.clReleaseProgram(program) );

	res = openCl.clBuildProgram(program, 1, deviceIdsPtr, null, null, null)

	checkError(res, 'clBuildProgram');

	let kernel = openCl.clCreateKernel(program, 'square', errPtr);

	checkError(errPtr, 'clCreateKernel');
	releaseList.push( () => openCl.clReleaseKernel(kernel) );

	let bufSize = dataTypeSize * data.length;

	let inBuf = Buffer.alloc(bufSize);
	data.forEach((v, i) => inBuf.writeFloatLE(v, dataTypeSize * i));

	let inClBuf = openCl.clCreateBuffer(ctx, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, inBuf.length, inBuf, errPtr);

	checkError(errPtr, 'clCreateBuffer In');
	releaseList.push( () => openCl.clReleaseMemObject(inClBuf) );

	let outClBuf = openCl.clCreateBuffer(ctx, CL_MEM_WRITE_ONLY, bufSize, null, errPtr);

	checkError(errPtr, 'clCreateBuffer Out');
	releaseList.push( () => openCl.clReleaseMemObject(outClBuf) );

	res = openCl.clSetKernelArg(kernel, 0, inBuf.length, inClBuf.ref());

	checkError(res, 'clSetKernelArg 0');

	res = openCl.clSetKernelArg(kernel, 1, bufSize, outClBuf.ref());

	checkError(res, 'clSetKernelArg 1');

	let ct = new Buffer(4);
	ct.writeUInt32LE(data.length);

	res = openCl.clSetKernelArg(kernel, 2, ct.length, ct);

	checkError(res, 'clSetKernelArg 2');

	let globalPtr = ref.alloc(sizeTPtr);
	sizeTPtr.set(globalPtr, 0, data.length);

	res = openCl.clEnqueueNDRangeKernel(queue, kernel, 1, null, globalPtr, null, 0, null, null);

	checkError(res, 'clEnqueueNDRangeKernel');

	let resBuf = Buffer.alloc(bufSize);

	res = openCl.clEnqueueReadBuffer(queue, outClBuf, true, 0, bufSize, resBuf, 0, null, null);

	checkError(res, 'clEnqueueReadBuffer');

	console.log(resBuf.readFloatLE());
	console.log(resBuf.readFloatLE(4));
	console.log(resBuf.readFloatLE(8));

} finally {
	releaseList.reverse().forEach( f => f() );
}
