#[derive(Clone)]
enum Tree<V> {
    Leaf(Leaf<V>),
    Node(Node<Box<Self>>),
}

#[derive(Clone)]
struct Leaf<V>(V);

#[derive(Clone)]
struct Node<T> {
    left: T,
    right: Option<T>,
}

trait TreeFunc<V> {
    fn values(&self) -> Vec<V>;
}

impl<V: Copy> TreeFunc<V> for Tree<V> {
    fn values(&self) -> Vec<V> {
        match self {
            Self::Leaf(x) => TreeFunc::<V>::values(x),
            Self::Node(x) => TreeFunc::<V>::values(x),
        }
    }
}

impl<V: Copy> TreeFunc<V> for Leaf<V> {
    fn values(&self) -> Vec<V> {
        vec![self.0]
    }
}

impl<V: Copy> TreeFunc<V> for Node<Box<Tree<V>>> {
    fn values(&self) -> Vec<V> {
        let v1 = self.left.values();
        let v2 = self.right.as_ref().map(|x| x.values()).unwrap_or_default();

        [v1, v2].concat()
    }
}

impl<V> From<Leaf<V>> for Tree<V> {
    fn from(value: Leaf<V>) -> Self {
        Self::Leaf(value)
    }
}

impl<V> From<Node<Box<Tree<V>>>> for Tree<V> {
    fn from(value: Node<Box<Tree<V>>>) -> Self {
        Self::Node(value)
    }
}

fn main() {
    let t1: Tree<i32> = Leaf(1).into();
    let t2: Tree<i32> = Leaf(2).into();
    let t3: Tree<i32> = Leaf(3).into();
    let t4: Tree<i32> = Leaf(4).into();

    let t5: Tree<i32> = Node {
        left: t4.clone().into(),
        right: None,
    }
    .into();

    let t6: Tree<i32> = Node {
        left: t3.clone().into(),
        right: Some(t5.clone().into()),
    }
    .into();

    let t7: Tree<i32> = Node {
        left: t6.clone().into(),
        right: Some(t2.clone().into()),
    }
    .into();

    let t8: Tree<i32> = Node {
        left: t1.clone().into(),
        right: Some(t7.clone().into()),
    }
    .into();

    println!("t1 values: {:?}", t1.values());
    println!("t5 values: {:?}", t5.values());
    println!("t6 values: {:?}", t6.values());
    println!("t7 values: {:?}", t7.values());
    println!("t8 values: {:?}", t8.values());
}
