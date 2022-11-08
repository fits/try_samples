
type Id = String;

pub enum Command {
    Create { id: Id },
    Update { id: Id, value: u32 },
}

fn value(c: &Command) -> u32 {
    let Command::Update { value: v, .. } = c else {
        return 0
    };

    v.clone()
}

fn main() {
    let c1 = Command::Create { id: "c1".to_string() };
    let c2 = Command::Update { id: "c2".to_string(), value: 123 };

    println!("{}", value(&c1));
    println!("{}", value(&c2));
}
