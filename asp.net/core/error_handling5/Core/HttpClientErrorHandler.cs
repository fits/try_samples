namespace Example.Core
{
    public class HttpClientErrorHandler : MessageProcessingHandler
    {
        const int STATUSCODE_SUCCESS_MAX = 226;

        protected override HttpRequestMessage ProcessRequest(HttpRequestMessage req, CancellationToken cancel) => req;

        protected override HttpResponseMessage ProcessResponse(HttpResponseMessage res, CancellationToken cancel) =>
            (int)res.StatusCode switch
            {
                <= STATUSCODE_SUCCESS_MAX => res,
                var v => throw new ExternalServiceException(res.RequestMessage?.RequestUri, v, res.Content),
            };
    }

    class ExternalServiceException(Uri? serviceUri, int statusCode, HttpContent content) : Exception($"failed invoke {serviceUri}"), IServiceError
    {
        public int StatusCode => statusCode;
        public HttpContent Content => content;
    }
}