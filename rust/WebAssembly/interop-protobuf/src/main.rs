
use prost::Message;

mod item {
    include!(concat!(env!("OUT_DIR"), "/sample.item.rs"));
}

fn new_item(id: &str, price: i32) -> item::Item {
    let mut d = item::Item::default();
    
    d.item_id = id.to_string();
    d.price = price;

    d
}

#[link(wasm_import_module = "sample")]
extern {
    fn log(ptr: *const u8, len: usize);
    fn print_item(ptr: *const u8, len: usize);
}

#[no_mangle]
extern fn _malloc(size: usize) -> *mut u8 {
    let buf = vec![0; size];
    Box::into_raw(buf.into_boxed_slice()) as *mut u8
}

#[no_mangle]
extern fn _free(ptr: *mut u8) {
    unsafe {
        drop(Box::from_raw(ptr));
    }
}

#[no_mangle]
extern fn send_item(ptr: *const u8, size: usize) {
    unsafe {
        let slice = std::slice::from_raw_parts(ptr, size);

        let d = item::Item::decode(slice).unwrap();

        let msg = format!("received '{:?}'", d);
        log(msg.as_ptr(), msg.len());
    }
}

fn main() {
    let d = new_item("test1", 5);

    let mut buf = Vec::with_capacity(d.encoded_len());

    d.encode(&mut buf).unwrap();

    unsafe {
        print_item(buf.as_ptr(), buf.len());
    }
}
