
extern crate hyper;

use hyper::{Body, Request, Response, Server, StatusCode};
use hyper::service::service_fn_ok;
use hyper::rt::{self, Future};

fn main() {
    let addr = ([127, 0, 0, 1], 3000).into();

    let svc = || {
        service_fn_ok(|req: Request<Body>| {

            let paths: Vec<_> = req.uri().path().split('/')
                                    .filter(|s| !s.is_empty()).collect();

            println!("{:?}", paths);

            match paths.as_slice() {
                ["users"] => Response::new(Body::from("users-all")),
                ["users", id] => Response::new(Body::from(format!("userid-{}", id))),
                _ => Response::builder()
                        .status(StatusCode::NOT_FOUND)
                        .body(Body::empty())
                        .unwrap()
            }
        })
    };

    let server = Server::bind(&addr)
        .serve(svc)
        .map_err(|e| eprintln!("server error: {}", e));

    rt::run(server);
}
