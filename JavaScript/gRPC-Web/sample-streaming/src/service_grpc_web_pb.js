/**
 * @fileoverview gRPC-Web generated client stub for sample
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!



const grpc = {};
grpc.web = require('grpc-web');


var event_pb = require('./event_pb.js')
const proto = {};
proto.sample = require('./service_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.sample.EventNotifyServiceClient =
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
proto.sample.EventNotifyServicePromiseClient =
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
 *   !proto.sample.SubscribeRequest,
 *   !proto.sample.DataEvent>}
 */
const methodDescriptor_EventNotifyService_subscribe = new grpc.web.MethodDescriptor(
  '/sample.EventNotifyService/subscribe',
  grpc.web.MethodType.SERVER_STREAMING,
  proto.sample.SubscribeRequest,
  event_pb.DataEvent,
  /**
   * @param {!proto.sample.SubscribeRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  event_pb.DataEvent.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.sample.SubscribeRequest,
 *   !proto.sample.DataEvent>}
 */
const methodInfo_EventNotifyService_subscribe = new grpc.web.AbstractClientBase.MethodInfo(
  event_pb.DataEvent,
  /**
   * @param {!proto.sample.SubscribeRequest} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  event_pb.DataEvent.deserializeBinary
);


/**
 * @param {!proto.sample.SubscribeRequest} request The request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!grpc.web.ClientReadableStream<!proto.sample.DataEvent>}
 *     The XHR Node Readable Stream
 */
proto.sample.EventNotifyServiceClient.prototype.subscribe =
    function(request, metadata) {
  return this.client_.serverStreaming(this.hostname_ +
      '/sample.EventNotifyService/subscribe',
      request,
      metadata || {},
      methodDescriptor_EventNotifyService_subscribe);
};


/**
 * @param {!proto.sample.SubscribeRequest} request The request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!grpc.web.ClientReadableStream<!proto.sample.DataEvent>}
 *     The XHR Node Readable Stream
 */
proto.sample.EventNotifyServicePromiseClient.prototype.subscribe =
    function(request, metadata) {
  return this.client_.serverStreaming(this.hostname_ +
      '/sample.EventNotifyService/subscribe',
      request,
      metadata || {},
      methodDescriptor_EventNotifyService_subscribe);
};


module.exports = proto.sample;

