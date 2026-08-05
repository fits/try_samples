mod monad;

use monad::*;

fn main() {
    let o1 = Option::unit(123);
    let o2 = o1.clone().bind(|x| Option::unit(format!("data-{}", x)));

    println!("{:?}, {:?}", o1, o2);

    let r1 = Result::<_, &str>::unit(true);
    let r2 = r1.clone().bind(|x| Result::unit(format!("result-{}", x)));

    println!("{:?}, {:?}", r1, r2);

    let v1 = Vec::unit(1);
    let v2 = v1
        .clone()
        .bind(|x| vec![x + 1, x + 2])
        .bind(|x| vec![x * 2, x * 3, x * 4]);

    println!("{:?}, {:?}", v1, v2);

    println!(
        "{:?}",
        Option::unit(1).bind(|x| Option::unit("a").bind(|y| Option::unit((x, y))))
    );

    let x1 = Option::unit(1).bind(|x| Monad::unit((x + 1, true)));

    println!("{:?}", x1);
}
