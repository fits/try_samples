
struct State<S, A>(Box<dyn Fn(S) -> (S, A)>);

impl<S: 'static, A: 'static> State<S, A> {
    fn map<B: 'static>(self, f: fn((S, A)) -> (S, B)) -> State<S, B> {
        let g = move |s| f(self.0(s));
        State(Box::new(g))
    }
}

fn main() {
    let f = |s: String| (format!("{}!", s), 1);

    let d = State(Box::new(f));
    println!("{:?}", d.0("sample1".to_string()));

    let f2 = |(s, i)| (format!("--{}", s), i * 5);

    let d2 = State::map(d, f2);
    println!("{:?}", d2.0("sample2".to_string()));

    let f3 = |(s, i)| (format!("{}***", s), format!("value:{}", i));

    let d3 = State::map(d2, f3);
    println!("{:?}", d3.0("sample3".to_string()));
}
