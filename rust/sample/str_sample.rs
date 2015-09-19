
fn main() {
	print_str("sample");
	print_str2("sample2");
	print_str3("sample3".to_string());

}

fn print_str(s: &str) {
	println!("print_str : {}", s);
}

fn print_str2(s: &'static str) {
	println!("print_str2 : {}", s);
}

fn print_str3(s: String) {
	println!("print_str3 : {}", s);
}
