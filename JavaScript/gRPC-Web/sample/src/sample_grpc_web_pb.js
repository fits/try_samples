/**
 * @fileoverview gRPC-Web generated client stub for sample1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!



const grpc = {};
grpc.web = require('grpc-web');

const proto = {};
proto.sample1 = require('./sample_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.sample1.SampleServiceClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.sample1.SampleServicePromiseClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.sample1.SampleRequest,
 *   !proto.sample1.SampleResponse>}
 */
const methodDescriptor_SampleService_Call = new grpc.web.MethodDescriptor(
  '/sample1.SampleService/Call',
  grpc.web.MethodType.UNARY,
  proto.sample1.SampleRequest,
  proto.sample1.SampleResponse,
  /**
   * @param {!proto.sample1.SampleRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.sample1.SampleResponse.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.sample1.SampleRequest,
 *   !proto.sample1.SampleResponse>}
 */
const methodInfo_SampleService_Call = new grpc.web.AbstractClientBase.MethodInfo(
  proto.sample1.SampleResponse,
  /**
   * @param {!proto.sample1.SampleRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.sample1.SampleResponse.deserializeBinary
);


/**
 * @param {!proto.sample1.SampleRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.sample1.SampleResponse)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.sample1.SampleResponse>|undefined}
 *     The XHR Node Readable Stream
 */
proto.sample1.SampleServiceClient.prototype.call =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/sample1.SampleService/Call',
      request,
      metadata || {},
      methodDescriptor_SampleService_Call,
      callback);
};


/**
 * @param {!proto.sample1.SampleRequest} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.sample1.SampleResponse>}
 *     A native promise that resolves to the response
 */
proto.sample1.SampleServicePromiseClient.prototype.call =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/sample1.SampleService/Call',
      request,
      metadata || {},
      methodDescriptor_SampleService_Call);
};


module.exports = proto.sample1;

