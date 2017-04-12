
extern crate hyper;

use std::env;
use hyper::server::{Server, Request, Response};

#[allow(unused_variables)]
fn sample(req: Request, res: Response) {
	res.send(b"sample page").unwrap();
}

fn main() {
	let host = env::args().nth(1).unwrap_or(String::from("127.0.0.1:8080"));

	let _ = Server::http(host).map(|s| s.handle(sample)).unwrap();
}
