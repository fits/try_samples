
pub trait Sample {
    fn type_name() -> &'static str;
    fn type_kind() -> &'static str;

    fn note(self: &Self) -> String;
}
