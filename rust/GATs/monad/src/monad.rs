pub trait Monad<A> {
    type Target<B>: Monad<B>;

    fn unit(value: A) -> Self;

    fn bind<F, B>(self, f: F) -> Self::Target<B>
    where
        F: Fn(A) -> Self::Target<B>;
}

impl<A> Monad<A> for Option<A> {
    type Target<B> = Option<B>;

    fn unit(value: A) -> Self {
        Some(value)
    }

    fn bind<F, B>(self, f: F) -> Self::Target<B>
    where
        F: Fn(A) -> Self::Target<B>,
    {
        self.and_then(f)
    }
}

impl<A, E> Monad<A> for Result<A, E> {
    type Target<B> = Result<B, E>;

    fn unit(value: A) -> Self {
        Ok(value)
    }

    fn bind<F, B>(self, f: F) -> Self::Target<B>
    where
        F: Fn(A) -> Self::Target<B>,
    {
        self.and_then(f)
    }
}

impl<A> Monad<A> for Vec<A> {
    type Target<B> = Vec<B>;

    fn unit(value: A) -> Self {
        vec![value]
    }

    fn bind<F, B>(self, f: F) -> Self::Target<B>
    where
        F: Fn(A) -> Self::Target<B>,
    {
        self.into_iter().flat_map(f).collect()
    }
}
