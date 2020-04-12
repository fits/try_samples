
use std::thread;
use std::sync::{Arc, Mutex};

fn sample1() {
    let data = Arc::new(vec![1, 2, 3, 4, 5]);
    let mut handles = vec![];

    for i in 0..5 {
        let d = data.clone();

        let handle = thread::spawn( move || {
            println!("sample1 - {}: thread_id={:?}, data={:?}", i, thread::current().id(), d);
        });

        handles.push(handle);
    }

    for h in handles {
        h.join().unwrap();
    }
}

fn sample2() {
    let data = Arc::new(Mutex::new(vec![1, 2, 3, 4, 5]));
    let mut handles = vec![];

    for i in 0..5 {
        let d = data.clone();

        let handle = thread::spawn( move || {
            let mut d = d.lock().unwrap();
            d[i] += 10;
            println!("sample2 - {}: thread_id={:?}, data={:?}", i, thread::current().id(), d);
        });

        handles.push(handle);
    }

    for h in handles {
        h.join().unwrap();
    }
}

fn main() {
    sample1();
    sample2();
}
