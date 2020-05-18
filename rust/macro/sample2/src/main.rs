
macro_rules! sample {
    (a => $a:expr) => {
        println!("a: {}", $a);
    };

    ($name:ident) => {
        fn $name(i: i32) -> i32 {
            i + 10
        }
    };

    ($x:expr) => {
        $x * 2
    };

    ( $( $x:expr ) , + ) => {
        {
            let mut total = 0;

            $(
                total += $x;
            )+

            total
        }
    };
}

fn main() {
    sample!(a => 1 + 2);

    sample!(a1);
    println!("a1(7) = {}", a1(7));

    println!("(2 + 3) * 2 = {}", sample!(2 + 3));
    println!("total = {}", sample!(1, 2, 3 * 2, 4 * 3, 5 - 3));
}