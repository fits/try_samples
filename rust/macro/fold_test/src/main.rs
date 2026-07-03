use proc_macro2::TokenStream;
use quote::{ToTokens, quote};
use syn::{Result, Signature, fold::Fold};

struct TraceReturnType;

impl Fold for TraceReturnType {
    fn fold_type(&mut self, i: syn::Type) -> syn::Type {
        println!("* fold_type");

        match i.clone() {
            syn::Type::Path(v) => {
                println!("Path: path={}", v.clone().path.into_token_stream());

                for s in v.path.segments {
                    println!("PathSegment: indent={}", s.ident);

                    match s.arguments {
                        syn::PathArguments::AngleBracketed(x) => {
                            println!("AngleBracketed: {}", x.args.to_token_stream());
                        }
                        syn::PathArguments::None => println!("argument is none"),
                        syn::PathArguments::Parenthesized(x) => {
                            println!("Parenthesized: {}", x.to_token_stream());
                        }
                    }
                }
            }
            _ => println!("other"),
        }

        syn::fold::fold_type(self, i)
    }
}

struct TraceTypePath;

impl Fold for TraceTypePath {
    fn fold_type_path(&mut self, i: syn::TypePath) -> syn::TypePath {
        println!("* fold_type_path");

        let p = i.clone().path;
        println!("ident: {}", p.get_ident().to_token_stream());

        for s in p.segments {
            self.fold_path_segment(s);
        }

        i
    }

    fn fold_path_segment(&mut self, i: syn::PathSegment) -> syn::PathSegment {
        println!("* fold_path_segment");

        println!("ident: {}", i.ident);

        self.fold_path_arguments(i.clone().arguments);

        i
    }

    fn fold_path_arguments(&mut self, i: syn::PathArguments) -> syn::PathArguments {
        println!("* fold_path_arguments");

        if let syn::PathArguments::AngleBracketed(x) = i.clone() {
            for a in x.args {
                self.fold_generic_argument(a);
            }
        }

        i
    }

    fn fold_generic_argument(&mut self, i: syn::GenericArgument) -> syn::GenericArgument {
        println!("* fold_generic_argument");

        match i.clone() {
            syn::GenericArgument::Type(x) => println!("type: {}", x.to_token_stream()),
            _ => println!("other"),
        }

        i
    }
}

#[derive(Debug, Default)]
struct SelfChecker(bool);

impl Fold for SelfChecker {
    fn fold_path_segment(&mut self, i: syn::PathSegment) -> syn::PathSegment {
        println!("* fold_path_segment: {}", i.to_token_stream());

        if self.0 {
            i
        } else if i.ident.to_string() == "Self" {
            self.0 = true;
            i
        } else {
            syn::fold::fold_path_segment(self, i)
        }
    }
}

fn parse_func(input: TokenStream) -> Result<Signature> {
    syn::parse2(input)
}

fn main() -> Result<()> {
    let mut folder = TraceReturnType;

    let f1 = parse_func(quote! { fn func1(&self, v: bool) -> Self })?;
    folder.fold_return_type(f1.output.clone());

    println!("---");

    let f2 = parse_func(quote! { fn func1(&self, v: bool) -> Result<Self, ()> })?;
    folder.fold_return_type(f2.output.clone());

    println!("---");

    let f3 =
        parse_func(quote! { fn func1(&self, v: bool) -> Result<Option<(Self, bool, isize)>, ()> })?;
    folder.fold_return_type(f3.output.clone());

    println!("---");

    let f4 = parse_func(
        quote! { fn func1(&self, v: bool) -> Result<Option<(String, bool, isize)>, ()> },
    )?;
    folder.fold_return_type(f4.output.clone());

    println!("------");

    let mut folder2 = TraceTypePath;

    folder2.fold_return_type(f1.output.clone());

    println!("---");

    folder2.fold_return_type(f2.output.clone());

    println!("---");

    folder2.fold_return_type(f3.output.clone());

    println!("---");

    folder2.fold_return_type(f4.output.clone());

    println!("------");

    let mut folder3 = SelfChecker::default();
    println!("{:?}", folder3);

    folder3.fold_return_type(f1.output.clone());
    println!("after f1: {:?}", folder3);

    println!("---");

    folder3 = SelfChecker::default();
    folder3.fold_return_type(f2.output.clone());
    println!("after f2: {:?}", folder3);

    println!("---");

    folder3 = SelfChecker::default();
    folder3.fold_return_type(f3.output.clone());
    println!("after f3: {:?}", folder3);

    println!("---");

    folder3 = SelfChecker::default();
    folder3.fold_return_type(f4.output.clone());
    println!("after f4: {:?}", folder3);

    Ok(())
}
