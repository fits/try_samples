
use std::thread;
use std::sync::{Arc, Mutex, RwLock};

struct Data(i32);

fn no_lock() {
    let mut hs = Vec::new();
    let ds = Arc::new(Vec::new());

    for i in 0..100 {
        let mut ds = ds.clone();

        hs.push(
            thread::spawn(move || {
                //ds.push(Data(i)) // compile error: cannot borrow as mutable
    
                if let Some(ds) = Arc::get_mut(&mut ds) {
                    ds.push(Data(i)) // not executed
                }
            })
        );
    }

    for h in hs {
        let _ = h.join();
    }

    println!("no_lock length = {}", ds.len());
}

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

    println!("use_mutex length = {}", ds.lock().unwrap().len());
}

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

    for _ in 0..3 {
        let ds = ds.clone();

        hs.push(
            thread::spawn(move || {
                if let Ok(ds) = ds.read() {
                    println!("progress length = {}", ds.len());
                }
            })
        );
    }

    for h in hs {
        let _ = h.join();
    }

    println!("use_rwlock length = {}", ds.read().unwrap().len());
}

fn use_rwlock2() {
    let ds = Arc::new(RwLock::new(Vec::new()));

    let hs = (0..100).map(|i| {
        let ds = ds.clone();

        thread::spawn(move || {
            if let Ok(mut ds) = ds.write() {
                ds.push(Data(i));
            }
        })
    });

    let rs = (0..3).map(|_| {
        let ds = ds.clone();

        thread::spawn(move || {
            if let Ok(ds) = ds.read() {
                println!("progress length = {}", ds.len());
            }
        })
    });

    for h in hs.chain(rs) {
        let _ = h.join();
    }

    println!("use_rwlock2 length = {}", ds.read().unwrap().len());
}

fn use_rwlock3() {
    let ds = Arc::new(RwLock::new(Vec::new()));

    let hs = (0..100).map(|i| {
        let ds = ds.clone();

        thread::spawn(move || {
            if let Ok(mut ds) = ds.write() {
                ds.push(Data(i));
            }
        })
    });

    let rs = (0..3).map(|_| {
        let ds = ds.clone();

        thread::spawn(move || {
            if let Ok(ds) = ds.read() {
                println!("progress length = {}", ds.len());
            }
        })
    });

    for h in rs.chain(hs) {
        let _ = h.join();
    }

    println!("use_rwlock3 length = {}", ds.read().unwrap().len());
}

fn main() {
    no_lock();

    println!("-----");

    use_mutex();

    println!("-----");

    use_rwlock();

    println!("-----");

    use_rwlock2();

    println!("-----");

    use_rwlock3();
}
