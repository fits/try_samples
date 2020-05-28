
use combine::{many1, Parser, sep_by, token, satisfy};
use combine::parser::char::{alpha_num, letter, digit, space, spaces};
use combine::parser::range::take_while1;

fn sample1() {
    let mut word = many1::<Vec<_>, _, _>(alpha_num());

    let res = word.parse("te1st data1");

    println!("1. {:?}", res);
    assert_eq!(res, Ok((vec!['t', 'e', '1', 's', 't'], " data1")));

    let res2 = many1::<String, _, _>(alpha_num()).parse("te1st data1");
    println!("2. {:?}", res2);
    assert_eq!(res2, Ok(("te1st".to_string(), " data1")));

    let res3 = many1::<String, _, _>(letter()).parse("te1st data1");
    println!("3. {:?}", res3);

    let res4 = many1::<String, _, _>(digit()).parse("te1st data1");
    println!("4. {:?}", res4);

    let res5 = take_while1(|c: char| c.is_alphanumeric()).parse("te1st data1");
    println!("5. {:?}", res5);
}

fn sample2() {
    let word = many1::<String, _, _>(alpha_num());
    let mut parser = sep_by::<Vec<_>, _, _, _>(word, space());

    let res = parser.parse("a1 b2 c3").unwrap();

    println!("{:?}", res);
}

fn sample3() {
    let word = many1::<String, _, _>(alpha_num());
    let sep = (spaces(), token(','), spaces());

    let mut parser = sep_by::<Vec<_>, _, _, _>(word, sep);

    let res = parser.parse("a1,b2,  c3  ,  d4").unwrap();

    println!("{:?}", res);
    
    assert_eq!(
        res, 
        (vec!["a1".to_string(), "b2".to_string(), "c3".to_string(), "d4".to_string()], "")
    );

    let res2 = parser.parse("a1,b2,  c-3  ,  d4").unwrap();

    println!("{:?}", res2);
}

fn sample4() {
    let sep = (spaces(), token(','), spaces());

    let word = many1::<String, _, _>(
        satisfy(|c: char| c.is_alphanumeric() || c == '_' || c == '-'
    ));

    let mut parser = sep_by::<Vec<_>, _, _, _>(word, sep);

    let res = parser.parse("a1,b2,  c-3  ,  _d4-").unwrap();

    println!("{:?}", res);

    assert_eq!(
        res, 
        (vec!["a1".to_string(), "b2".to_string(), "c-3".to_string(), "_d4-".to_string()], "")
    );
}

fn main() {
    sample1();
    sample2();

    sample3();
    sample4();
}
