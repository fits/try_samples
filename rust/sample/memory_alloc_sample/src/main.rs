use std::ptr;
use std::mem;
use std::alloc;
use libc;

fn alloc(size: usize) -> *mut u8 {
    let align = mem::align_of::<usize>();
    let layout = alloc::Layout::from_size_align(size, align).unwrap();

    unsafe {
        alloc::alloc(layout)
    }
}

fn dealloc(ptr: *mut u8, size: usize) {
    let align = mem::align_of::<usize>();
    let layout = alloc::Layout::from_size_align(size, align).unwrap();

    unsafe {
        ptr::drop_in_place(ptr);
        alloc::dealloc(ptr, layout);
    }
}

fn alloc_box(len: usize) -> *mut u8 {
    Box::into_raw(vec![0; len].into_boxed_slice()) as *mut u8
}

fn alloc_box2(len: usize) -> *mut u8 {
    let mut v = Vec::<u8>::with_capacity(len);

    unsafe {
        v.set_len(len);
    }
    
    Box::into_raw(v.into_boxed_slice()) as *mut u8
}

fn dealloc_box(ptr: *mut u8, _size: usize) {
    unsafe {
        drop(Box::from_raw(ptr));
    }
}

fn alloc_libc(size: usize) -> *mut u8 {
    unsafe {
        libc::malloc(size) as *mut u8
    }
}

fn dealloc_libc(ptr: *mut u8, _size: usize) {
    unsafe {
        libc::free(ptr as *mut libc::c_void)
    }
}

fn consume(ptr: *mut u8, len: usize) {
    unsafe {
        let s = std::slice::from_raw_parts(ptr, len);
        println!("{:?}", s);
    }
}

fn write(ptr: *mut u8, len: usize) {
    unsafe {
        for i in 0..len {
            ptr.add(i).write((i + 11) as u8);
        }
    }
}

fn exec(n: usize, alloc_f: fn (usize) -> *mut u8, 
    dealloc_f: fn (*mut u8, usize)) {

    let p = alloc_f(n);

    write(p, n);
    consume(p, n);
    consume(p, n);

    dealloc_f(p, n);

    consume(p, n);
}

fn main() {
    println!("--- alloc, dealloc ---");

    exec(5, alloc, dealloc);

    println!("--- Box ---");

    exec(5, alloc_box, dealloc_box);

    println!("--- Box with_capacity ---");

    exec(5, alloc_box2, dealloc_box);

    println!("--- libc::malloc, free ---");

    exec(5, alloc_libc, dealloc_libc);
}
