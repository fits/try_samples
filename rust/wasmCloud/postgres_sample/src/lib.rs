use wasmcloud::postgres::query::{PgValue, query};
use wasmcloud_component::http;

wit_bindgen::generate!({
    world: "runquery",
    generate_all,
});

struct Component;

http::export!(Component);

impl http::Server for Component {
    fn handle(
        request: http::IncomingRequest,
    ) -> http::Result<http::Response<impl http::OutgoingBody>> {
        let (parts, _) = request.into_parts();
        let path = parts.uri.path();

        query(
            "SELECT * FROM urls WHERE path = $1",
            &[PgValue::Text(path.to_string())],
        )
        .map(|rows| {
            let res = rows
                .iter()
                .map(|r| {
                    r.iter()
                        .map(|c| {
                            format!("{}={}", c.column_name, value_to_string(c.value.to_owned()))
                        })
                        .collect::<Vec<_>>()
                        .join(",")
                })
                .collect::<Vec<_>>()
                .join("; ");

            http::Response::new(res)
        })
        .map_err(|e| http::ErrorCode::InternalError(Some(e.to_string())))
    }
}

fn value_to_string(value: PgValue) -> String {
    match value {
        PgValue::Varchar((_, b)) => format!("varchar:{:?}", String::from_utf8(b)),
        PgValue::Int4(n) => format!("int4:{}", n),
        _ => format!("other:{:?}", value),
    }
}
