
use proc_macro::TokenStream;
use quote::quote;
use syn::{parse_macro_input, Ident, LitStr};

#[proc_macro]
pub fn sample_func2(input: TokenStream) -> TokenStream {
    println!("*** sample_func2 input: {:?}", input);

    let input = parse_macro_input!(input as LitStr);
    let name = Ident::new(&input.value(), proc_macro2::Span::call_site());

    let code = quote! {
        fn #name(n: i32) -> i32 {
            n + 20
        }
    };

    code.into()
}
