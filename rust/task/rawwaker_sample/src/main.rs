
use std::ptr::null;
use std::future::Future;
use std::task::{Context, Waker, RawWaker, RawWakerVTable};

unsafe fn sample_clone(data: *const ()) -> RawWaker {
    println!("*** call clone: {:?}", data);
    sample_raw_waker()
}

unsafe fn sample_wake(data: *const ()) {
    println!("*** call wake: {:?}", data);
}

unsafe fn sample_wake_by_ref(data: *const ()) {
    println!("*** call wake_by_ref: {:?}", data);
}

unsafe fn sample_drop(data: *const ()) {
    println!("*** call drop: {:?}", data);
}

const SAMPLE_WAKER_VTABLE: RawWakerVTable =
    RawWakerVTable::new(
        sample_clone, 
        sample_wake, 
        sample_wake_by_ref, 
        sample_drop
    );

fn sample_raw_waker() -> RawWaker {
    RawWaker::new(null(), &SAMPLE_WAKER_VTABLE)
}

fn sample_waker() -> Waker {
    unsafe {
        Waker::from_raw(sample_raw_waker())
    }
}

fn main() {
    let waker = sample_waker();
    let mut ctx = Context::from_waker(&waker);

    let mut f1 = Box::pin(async { 11 });

    let r1 = f1.as_mut().poll(&mut ctx);

    println!("r1 = {:?}", r1);

    let r2 = std::panic::catch_unwind(move || {
        f1.as_mut().poll(&mut ctx)
    });

    println!("r2 = {:?}", r2);
}