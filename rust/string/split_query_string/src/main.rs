fn main() {
    let s1 = "a=1&b=2&c";
    println!("\"{}\" -> {:?}", s1, parse_params(s1));
    // "a=1&b=2&c" -> [("a", "1"), ("b", "2")]

    let s2 = "a=1&b=";
    println!("\"{}\" -> {:?}", s2, parse_params(s2));
    // "a=1&b=" -> [("a", "1"), ("b", "")]

    let s3 = "a=1";
    println!("\"{}\" -> {:?}", s3, parse_params(s3));
    // "a=1" -> [("a", "1")]

    let s4 = "a";
    println!("\"{}\" -> {:?}", s4, parse_params(s4));
    // "a" -> []
}

fn parse_params(s: &str) -> Vec<(&str, &str)> {
    s.split("&")
        .filter(|t| t.contains("="))
        .map(|t| {
            let v = t.split("=").collect::<Vec<_>>();
            (v[0], v[1])
        })
        .collect()
}
