use proc_macro::TokenStream;

use quote::quote;
use syn::parse::{Parse, ParseStream};
use syn::{Ident, ItemFn, parse_macro_input};

#[derive(Debug)]
struct CustomArgs {
    value: Ident,
}

impl Parse for CustomArgs {
    fn parse(input: ParseStream) -> syn::Result<Self> {
        let v: Ident = input.parse()?;

        Ok(Self { value: v })
    }
}

#[proc_macro_attribute]
pub fn custom(attr: TokenStream, input: TokenStream) -> TokenStream {
    let attr_args = parse_macro_input!(attr as CustomArgs);
    let ast = parse_macro_input!(input as ItemFn);

    let fn_name = attr_args.value;

    let sig = ast.sig.clone();

    let st = ast
        .block
        .stmts
        .iter()
        .fold(String::new(), |acc, x| format!("{acc} {}", quote! { #x }));

    let code = quote! {
        fn #fn_name() {
            println!("* sig: {}", stringify!(#sig));
            println!("* stmt: {}", #st);
        }
    };

    let fn_name2 = syn::parse_str::<Ident>(&format!("{}_2", fn_name)).unwrap();

    let code2 = quote! {
        fn #fn_name2() {
            println!("*** called {}", stringify!(#fn_name2));
        }
    };

    quote! {
        #ast
        #code
        #code2
    }
    .into()
}
