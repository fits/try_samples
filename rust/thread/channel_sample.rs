
use std::thread;
use std::sync::mpsc;
use std::time::Duration;

fn sample1() {
    let n = 5;
    let (tx, rx) = mpsc::channel();

    for i in 0..n {
        let tx = tx.clone();

        thread::spawn(move || {
            println!("sample1-send: {}", i);
            tx.send(i)
        });
    }

    for _ in 0..n {
        let res = rx.recv().unwrap();
        println!("sample1-receive: {}", res);
    }
}

fn sample2() {
    let n = 5;
    let (tx, rx) = mpsc::channel();

    for i in 0..n {
        let tx = tx.clone();

        thread::spawn(move || {
            println!("sample2-send: {}", i);
            tx.send(i)
        });
    }

    thread::sleep(Duration::from_millis(1));

    for r in rx.try_iter() {
        println!("sample2-receive: {}", r);
    }
}

fn main() {
    sample1();
    sample2();
}
