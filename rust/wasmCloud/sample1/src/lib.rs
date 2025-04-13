use wasmcloud_component::http;

struct Component;

http::export!(Component);

impl http::Server for Component {
    fn handle(
        request: http::IncomingRequest,
    ) -> http::Result<http::Response<impl http::OutgoingBody>> {
        let (parts, _) = request.into_parts();

        let r = match parts.uri.path() {
            "/a" => "pathA",
            "/b" => "pathB",
            _ => "unknown path",
        };

        Ok(http::Response::new(r))
    }
}
