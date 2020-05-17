
use proc_macro::TokenStream;
use quote::quote;
use syn::{parse_macro_input, Ident};

#[proc_macro]
pub fn sample_func(input: TokenStream) -> TokenStream {
    println!("*** sample_func input: {:?}", input);

    let name = parse_macro_input!(input as Ident);

    let code = quote! {
        fn #name(n: i32) -> i32 {
            n + 10
        }
    };

    code.into()
}
