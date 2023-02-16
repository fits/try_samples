
wit_bindgen::generate!("sample");

struct SampleHost;

impl Sample for SampleHost {
    fn run() {
        log(LogLevel::Info, "test log");
    }
}

export_sample!(SampleHost);
