
fn main() {
    let s = r"
# sample1

markdown sample

* item-1
* item-2
    * item-2-1
    ";

    let parser = pulldown_cmark::Parser::new(s);

    let mut res = String::new();

    pulldown_cmark::html::push_html(&mut res, parser);

    println!("{}", res);
}
