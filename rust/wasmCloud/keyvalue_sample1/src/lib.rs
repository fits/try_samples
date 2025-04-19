use wasmcloud_component::http;
use wasmcloud_component::http::ErrorCode;
use wasmcloud_component::wasi::keyvalue::*;

struct Component;

http::export!(Component);

fn internal_err(msg: String) -> ErrorCode {
    ErrorCode::InternalError(Some(msg))
}

impl http::Server for Component {
    fn handle(
        request: http::IncomingRequest,
    ) -> http::Result<http::Response<impl http::OutgoingBody>> {
        let key = request.uri().path();

        let bucket = store::open("default").map_err(|e| internal_err(e.to_string()))?;

        let count = atomics::increment(&bucket, key, 1).map_err(|e| internal_err(e.to_string()))?;

        Ok(http::Response::new(format!("key={key}, count={count}")))
    }
}
