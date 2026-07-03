use proc_macro2::TokenStream;
use quote::{ToTokens, format_ident, quote};
use syn::{Ident, Result, Signature, fold::Fold};

struct SelfReplace(Ident);

impl Fold for SelfReplace {
    fn fold_path_segment(&mut self, i: syn::PathSegment) -> syn::PathSegment {
        if i.ident.to_string() == "Self" {
            let mut res = i.clone();
            res.ident = self.0.clone();
            res
        } else {
            syn::fold::fold_path_segment(self, i)
        }
    }
}

fn parse_func(input: TokenStream) -> Result<Signature> {
    syn::parse2(input)
}

fn main() -> Result<()> {
    let mut folder = SelfReplace(format_ident!("Data1"));

    let f1 = parse_func(quote! { fn func1(&self, v: bool) -> (i32, bool) })?;
    println!(
        "f1: {}",
        folder.fold_return_type(f1.output).to_token_stream()
    );

    let f2 = parse_func(quote! { fn func1(&self, v: bool) })?;
    println!(
        "f2: {}",
        folder.fold_return_type(f2.output).to_token_stream()
    );

    let f3 = parse_func(quote! { fn func1(&self, v: bool) -> Self })?;
    println!(
        "f3: {}",
        folder.fold_return_type(f3.output).to_token_stream()
    );

    let f4 = parse_func(quote! { fn func1(&self, v: bool) -> Option<Self> })?;
    println!(
        "f4: {}",
        folder.fold_return_type(f4.output).to_token_stream()
    );

    let f5 = parse_func(
        quote! { fn func1(&self, v: bool) -> Result<Option<(bool, Self, i32)>, String> },
    )?;
    println!(
        "f5: {}",
        folder.fold_return_type(f5.output).to_token_stream()
    );

    let f6 = parse_func(
        quote! { fn func1(&self, v: bool) -> (isize, Option<Result<(bool, Self), ()>>, Self) },
    )?;
    println!(
        "f6: {}",
        folder.fold_return_type(f6.output).to_token_stream()
    );

    let f7 = parse_func(quote! { fn func1(&self, v: bool) -> Option<(i32, bool, (String, f32))> })?;
    println!(
        "f7: {}",
        folder.fold_return_type(f7.output).to_token_stream()
    );

    Ok(())
}
