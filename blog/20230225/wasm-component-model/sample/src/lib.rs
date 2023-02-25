
wit_bindgen::generate!("sample");

struct Component;

impl Sample for Component {
    fn run() {
        log(LogLevel::Debug, "called run");

        let r = getitem("item-1");

        log(LogLevel::Info, &format!("item: {:?}", r));
    }
}

export_sample!(Component);
