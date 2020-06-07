
use std::slice;

fn split(v: &mut [i32]) -> (&[i32], &[i32]) {
    let p = v.len() / 2;

    (&v[..p], &v[p..])
}

fn split_mut(v: &mut [i32]) -> (&mut [i32], &mut [i32]) {
    let p = v.len() / 2;

    let ptr = v.as_mut_ptr();

    unsafe {
        let s1 = slice::from_raw_parts_mut(ptr, p);

        let next_p = ptr.offset(p as isize);
        let s2 = slice::from_raw_parts_mut(next_p, v.len() - p);

        (s1, s2)
    }
}

fn main() {
    let mut v = vec![1, 2, 3, 4, 5];

    println!("{:?}", split(&mut v));
    println!("{:?}", split(&mut [1]));
    println!("{:?}", split(&mut []));

    println!("-----");

    println!("{:?}", split_mut(&mut v));
    println!("{:?}", split_mut(&mut [1]));
    println!("{:?}", split_mut(&mut []));

    println!("-----");

    let (_, s2) = split_mut(&mut v);
    println!("{:?}", s2);

    if let Some(s) = s2.last_mut() {
        *s = 7;
    }

    println!("{:?}", s2);
}
