
use std::net::TcpStream;

fn main() {
    let res = TcpStream::connect("127.0.0.1:8080");
    println!("{:?}", res);
}