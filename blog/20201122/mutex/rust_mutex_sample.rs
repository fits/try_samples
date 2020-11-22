
use std::thread;
use std::sync::{Arc, Mutex, RwLock};

struct Data(i32);

// (a)
fn no_lock() {
    let mut hs = Vec::new();
    let ds = Arc::new(Vec::new());

    for i in 0..100 {
        let mut ds = ds.clone();

        hs.push(
            thread::spawn(move || {
                if let Some(ds) = Arc::get_mut(&mut ds) {
                    ds.push(Data(i)) // not executed
                }
            })
        );
    }

    for h in hs {
        let _ = h.join();
    }

    println!("(a) no_lock length = {}", ds.len());
}

// (b)
fn use_mutex() {
    let mut hs = Vec::new();
    let ds = Arc::new(Mutex::new(Vec::new()));

    for i in 0..100 {
        let ds = ds.clone();

        hs.push(
            thread::spawn(move || {
                if let Ok(mut ds) = ds.lock() {
                    ds.push(Data(i));
                }
            })
        );
    }

    for h in hs {
        let _ = h.join();
    }

    println!("(b) use_mutex length = {}", ds.lock().unwrap().len());
}

// (c)
fn use_rwlock() {
    let mut hs = Vec::new();
    let ds = Arc::new(RwLock::new(Vec::new()));

    for i in 0..100 {
        let ds = ds.clone();

        hs.push(
            thread::spawn(move || {
                if let Ok(mut ds) = ds.write() {
                    ds.push(Data(i));
                }
            })
        );
    }

    for _ in 0..5 {
        let ds = ds.clone();

        hs.push(
            thread::spawn(move || {
                if let Ok(ds) = ds.read() {
                    println!("(c) progress length = {}", ds.len());
                }
            })
        );
    }

    for h in hs {
        let _ = h.join();
    }

    println!("(c) use_rwlock length = {}", ds.read().unwrap().len());
}

fn main() {
    no_lock();

    println!("-----");

    use_mutex();

    println!("-----");

    use_rwlock();
}
