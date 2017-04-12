
extern crate hyper;

use hyper::server::{Server, Request, Response};

#[allow(unused_variables)]
fn sample(req: Request, res: Response) {
	res.send(b"sample page").unwrap();
}

fn main() {
	let _ = Server::http("127.0.0.1:8080").map(|s| s.handle(sample)).unwrap();
}
