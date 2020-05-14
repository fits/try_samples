
use proc_macro2::{Ident, Span};
use quote::{format_ident, quote};

fn main() {
    let i1 = Ident::new("d1", Span::call_site());
    println!("{}", i1);

    let exp = quote! { let #i1 = 123; };
    println!("{}", exp);

    let i2 = format_ident!("{}_copy", i1);
    println!("{}", i2);
}