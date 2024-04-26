#[allow(warnings)]
mod bindings;

use bindings::exports::wasi::http::incoming_handler::Guest;
use bindings::wasi::http::types::{ErrorCode, HeaderError, Headers, OutgoingResponse, ResponseOutparam};
use bindings::wasi::io::streams::StreamError;

struct Component;

impl Guest for Component {
    fn handle(
        request: bindings::exports::wasi::http::incoming_handler::IncomingRequest,
        response_out: bindings::exports::wasi::http::incoming_handler::ResponseOutparam,
    ) {
        let s = format!("ok:{}", request.path_with_query().unwrap_or_default());

        ResponseOutparam::set(response_out, create_response(s));
    }
}

fn create_response(s: String) -> Result<OutgoingResponse, ErrorCode> {
    let buf = s.as_bytes();

    let h = Headers::new();
    h.append(&"content-length".to_string(), &buf.len().to_string().into())?;

    let r = OutgoingResponse::new(h);

    let b = r.body()?;
    let w = b.write()?;

    w.write(buf)?;
    w.flush()?;

    Ok(r)
}

impl From<()> for ErrorCode {
    fn from(_value: ()) -> Self {
        ErrorCode::InternalError(None)
    }
}

impl From<StreamError> for ErrorCode {
    fn from(value: StreamError) -> Self {
        ErrorCode::InternalError(Some(value.to_string()))
    }
}

impl From<HeaderError> for ErrorCode {
    fn from(value: HeaderError) -> Self {
        ErrorCode::InternalError(Some(value.to_string()))
    }
}

bindings::export!(Component with_types_in bindings);
