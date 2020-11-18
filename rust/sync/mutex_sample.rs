
use std::thread;
use std::sync::{Arc, Mutex, RwLock};

struct Data(i32);

fn no_lock() {
    let ds = Arc::new(Vec::new());

    let hs = (0..100).map(|i| {
        let mut ds = ds.clone();

        thread::spawn(move || {
            //ds.push(Data(i)) // compile error: cannot borrow as mutable

            if let Some(ds) = Arc::get_mut(&mut ds) {
                ds.push(Data(i)) // not executed
            }
        })
    });

    for h in hs {
        let _ = h.join();
    }

    println!("no_lock length = {}", ds.len());
}

fn use_mutex() {
    let ds = Arc::new(Mutex::new(Vec::new()));

    let hs = (0..100).map(|i| {
        let ds = ds.clone();

        thread::spawn(move || {
            if let Ok(mut ds) = ds.lock() {
                ds.push(Data(i));
            }
        })
    });

    for h in hs {
        let _ = h.join();
    }

    println!("use_mutex length = {}", ds.lock().unwrap().len());
}

fn use_rwlock() {
    let ds = Arc::new(RwLock::new(Vec::new()));

    let hs = (0..100).map(|i| {
        let ds = ds.clone();

        thread::spawn(move || {
            if let Ok(mut ds) = ds.write() {
                ds.push(Data(i));
            }
        })
    });

    for h in hs {
        let _ = h.join();
    }

    println!("use_rwlock length = {}", ds.read().unwrap().len());
}

fn main() {
    no_lock();

    println!("-----");

    use_mutex();

    println!("-----");

    use_rwlock();
}
