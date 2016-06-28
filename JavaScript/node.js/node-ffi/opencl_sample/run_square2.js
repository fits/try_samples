
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
const FloatArray = ArrayType('float');

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

const data = [1.1, 2.2, 3.3, 4.4, 5.5];

const code = fs.readFileSync(process.argv[2]);

const releaseList = [];

try {
	const platformIdsPtr = ref.alloc(sizeTPtr);

	let res = openCl.clGetPlatformIDs(1, platformIdsPtr, null);

	checkError(res, 'clGetPlatformIDs');

	const platformId = sizeTPtr.get(platformIdsPtr);

	const deviceIdsPtr = ref.alloc(sizeTPtr);

	res = openCl.clGetDeviceIDs(platformId, CL_DEVICE_TYPE_DEFAULT, 1, deviceIdsPtr, null);

	checkError(res, 'clGetDeviceIDs');

	const deviceId = sizeTPtr.get(deviceIdsPtr);

	const errPtr = ref.alloc(intPtr);

	const ctx = openCl.clCreateContext(null, 1, deviceIdsPtr, null, null, errPtr);

	checkError(errPtr, 'clCreateContext');
	releaseList.push( () => openCl.clReleaseContext(ctx) );

	const queue = openCl.clCreateCommandQueue(ctx, deviceId, 0, errPtr);

	checkError(errPtr, 'clCreateCommandQueue');
	releaseList.push( () => openCl.clReleaseCommandQueue(queue) );

	const codeArray = new StringArray([code.toString()]);

	const program = openCl.clCreateProgramWithSource(ctx, 1, codeArray, null, errPtr);

	checkError(errPtr, 'clCreateProgramWithSource');
	releaseList.push( () => openCl.clReleaseProgram(program) );

	res = openCl.clBuildProgram(program, 1, deviceIdsPtr, null, null, null)

	checkError(res, 'clBuildProgram');

	const kernel = openCl.clCreateKernel(program, 'square', errPtr);

	checkError(errPtr, 'clCreateKernel');
	releaseList.push( () => openCl.clReleaseKernel(kernel) );

	const dataArray = new FloatArray(data);
	const bufSize = dataArray.buffer.length;

	const inClBuf = openCl.clCreateBuffer(ctx, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, bufSize, dataArray.buffer, errPtr);

	checkError(errPtr, 'clCreateBuffer In');
	releaseList.push( () => openCl.clReleaseMemObject(inClBuf) );

	const outClBuf = openCl.clCreateBuffer(ctx, CL_MEM_WRITE_ONLY, bufSize, null, errPtr);

	checkError(errPtr, 'clCreateBuffer Out');
	releaseList.push( () => openCl.clReleaseMemObject(outClBuf) );

	res = openCl.clSetKernelArg(kernel, 0, bufSize, inClBuf.ref());

	checkError(res, 'clSetKernelArg 0');

	res = openCl.clSetKernelArg(kernel, 1, bufSize, outClBuf.ref());

	checkError(res, 'clSetKernelArg 1');

	const ct = ref.alloc(ref.types.uint32, data.length);

	res = openCl.clSetKernelArg(kernel, 2, ct.length, ct.ref());

	checkError(res, 'clSetKernelArg 2');

	const globalPtr = ref.alloc(sizeTPtr);
	sizeTPtr.set(globalPtr, 0, data.length);

	res = openCl.clEnqueueNDRangeKernel(queue, kernel, 1, null, globalPtr, null, 0, null, null);

	checkError(res, 'clEnqueueNDRangeKernel');

	const resBuf = Buffer.alloc(bufSize);

	res = openCl.clEnqueueReadBuffer(queue, outClBuf, true, 0, bufSize, resBuf, 0, null, null);

	checkError(res, 'clEnqueueReadBuffer');

	for (let i = 0; i < data.length; i++) {
		console.log(ref.types.float.get(resBuf, i * ref.types.float.size));
	}

} finally {
	releaseList.reverse().forEach( f => f() );
}
