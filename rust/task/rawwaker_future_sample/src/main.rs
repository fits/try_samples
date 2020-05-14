
use std::ptr::null;
use std::pin::Pin;
use std::future::Future;
use std::task::{Context, Waker, RawWaker, RawWakerVTable, Poll};
use std::marker::Unpin;

const SAMPLE_WAKER_VTABLE: RawWakerVTable =
    RawWakerVTable::new(
        |d| {
            println!("*** call clone: {:?}", d);
            sample_raw_waker()
        }, 
        |d| println!("*** call wake: {:?}", d), 
        |_| println!("*** call wake_by_ref"), 
        |d| println!("*** call drop: {:?}", d)
    );

fn sample_raw_waker() -> RawWaker {
    RawWaker::new(null(), &SAMPLE_WAKER_VTABLE)
}

fn sample_waker() -> Waker {
    unsafe {
        Waker::from_raw(sample_raw_waker())
    }
}

struct SampleFuture<T> {
    value: T,
    polled: bool,
}

impl<T> SampleFuture<T> {
    fn new(value: T) -> Self {
        Self { value, polled: false }
    }
}

impl<T> Future for SampleFuture<T> 
where
    T: Unpin + Clone
{
    type Output = T;

    fn poll(self: Pin<&mut Self>, ctx: &mut Context<'_>) -> Poll<Self::Output> {
        let state = self.get_mut();

        if state.polled {
            Poll::Ready(state.value.clone())
        } else {
            state.polled = true;
            ctx.waker().clone().wake();
            //ctx.waker().wake_by_ref();
            Poll::Pending
        }
    }
}
fn main() {
    let waker = sample_waker();
    let mut ctx = Context::from_waker(&waker);

    let mut f1 = Box::pin(SampleFuture::new(12));

    let r1 = f1.as_mut().poll(&mut ctx);
    println!("r1 = {:?}", r1);

    let r2 = f1.as_mut().poll(&mut ctx);
    println!("r2 = {:?}", r2);
}