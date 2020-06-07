
use std::mem;
use std::alloc;

#[allow(dead_code)]
struct Data {
    v1: i32,
    v2: u8,
}

fn main() {
    println!("usize align = {}", mem::align_of::<usize>());
    println!("u8 align = {}", mem::align_of::<u8>());

    println!("-----");

    println!("{:?}", alloc::Layout::new::<i32>());

    println!("-----");

    println!("{:?}", alloc::Layout::from_size_align(10, 1).unwrap());

    println!("-----");

    println!("{:?}", alloc::Layout::for_value(&vec![1, 2, 3]));
    println!("{:?}", alloc::Layout::for_value(&vec![0; 5]));
    println!("{:?}", alloc::Layout::for_value(&vec![0; 5].into_boxed_slice()));

    println!("-----");

    println!("{:?}", alloc::Layout::new::<Data>());

    println!("-----");

    println!("{:?}", alloc::Layout::for_value(&(1, 2, 3)));
    println!("{:?}", alloc::Layout::for_value(&(1u8, 2u8, 3u8)));
    println!("{:?}", alloc::Layout::for_value(&(1u8, 2u8, 3u8, 4)));
}