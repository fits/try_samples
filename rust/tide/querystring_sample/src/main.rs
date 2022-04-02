
use tide::http::{self, convert::Deserialize};
use tide::Request;

#[derive(Debug, Deserialize)]
struct Param {
    page: u32,
    offset: Option<u32>,
    q: Option<Vec<String>>,
}

fn main() {
    let r1: Request<()> = http::Request::get("http://localhost/sample?page=1&offset=50").into();
    let q1: Param = r1.query().unwrap();
    println!("{:?}", q1);

    let r2: Request<()> = http::Request::get("http://localhost/sample?page=2").into();
    let Param { page, offset, q } = r2.query().unwrap();
    println!("page={:?}, offset={:?}, q={:?}", page, offset, q);

    let r3: Request<()> = http::Request::get("http://localhost/sample?page=3&q[0]=a1&q[1]=b2&q[2]=c3").into();
    let Param { q, .. } = r3.query().unwrap();
    println!("q={:?}", q);
}
