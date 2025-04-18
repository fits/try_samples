use exports::wasi::http::incoming_handler::Guest;
use wasi::http::types::{ErrorCode, Headers, OutgoingResponse, ResponseOutparam};

wit_bindgen::generate!({
    world: "counter",
    generate_all
});

struct CounterHost;

impl Guest for CounterHost {
    fn handle(
        request: exports::wasi::http::incoming_handler::IncomingRequest,
        response_out: exports::wasi::http::incoming_handler::ResponseOutparam,
    ) -> () {
        if let Some(id) = request.path_with_query() {
            ResponseOutparam::set(
                response_out,
                countup(&id).map_err(|e| ErrorCode::InternalError(Some(e.to_string()))),
            );
        } else {
            ResponseOutparam::set(response_out, Err(ErrorCode::HttpRequestUriInvalid));
        }
    }
}

fn countup(id: &str) -> Result<OutgoingResponse, Box<dyn std::error::Error>> {
    let bucket = wasi::keyvalue::store::open("")?;

    let count = wasi::keyvalue::atomics::increment(&bucket, id, 1)?;

    let res = format!("count={}, get={:?}", count, bucket.get(id));

    let h = Headers::new();
    h.append("content-length", res.len().to_string().as_bytes())?;

    let r = OutgoingResponse::new(h);

    let b = r.body().map_err(|_| "failed body")?;
    let w = b.write().map_err(|_| "failed write")?;

    w.write(res.as_bytes())?;
    w.flush()?;

    Ok(r)
}

export!(CounterHost);
