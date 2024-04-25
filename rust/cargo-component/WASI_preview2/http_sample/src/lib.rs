#[allow(warnings)]
mod bindings;

use bindings::exports::wasi::http::incoming_handler::Guest;
use bindings::wasi::http::types::{Headers, OutgoingResponse, ResponseOutparam};

struct Component;

impl Guest for Component {
    fn handle(
        _request: bindings::exports::wasi::http::incoming_handler::IncomingRequest,
        response_out: bindings::exports::wasi::http::incoming_handler::ResponseOutparam,
    ) {
        let h = Headers::new();
        let _ = h.append(&"sample".to_string(), &"123".into());
    
        let r = OutgoingResponse::new(h);

        ResponseOutparam::set(response_out, Ok(r));
    }
}

bindings::export!(Component with_types_in bindings);
