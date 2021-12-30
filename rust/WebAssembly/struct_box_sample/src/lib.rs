struct Data {
    value: i32
}

#[no_mangle]
extern fn new_data(value: i32) -> *mut Data {
    let d = Box::new(Data { value });
    Box::into_raw(d)
}

#[no_mangle]
extern fn drop_data(ptr: *mut Data) {
    unsafe {
        drop(Box::from_raw(ptr));
    }
}

#[no_mangle]
extern fn take_value(ptr: *mut Data) -> i32 {
    unsafe {
        let d = Box::from_raw(ptr);
        let v = d.value;

        Box::into_raw(d);

        v
    }
}
