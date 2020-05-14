
use proc_macro::TokenStream;
use quote::quote;
use syn::{parse_macro_input, DeriveInput, Data, Fields};

#[proc_macro_derive(Sample)]
pub fn sample_derive(input: TokenStream) -> TokenStream {
    let ast = parse_macro_input!(input as DeriveInput);
    //let ast: DeriveInput = syn::parse(input).unwrap();

    let name = &ast.ident;

    let data_type = match &ast.data {
        Data::Struct(s) => match s.fields {
            Fields::Named(_) => "struct-named",
            Fields::Unnamed(_) => "struct-un_named",
            Fields::Unit => "struct-unit",
        },
        Data::Enum(_) => "enum",
        Data::Union(_) => "union",
    };

    let code = quote! {
        impl Sample for #name {
            fn type_name() -> &'static str {
                stringify!(#name)
            }

            fn type_kind() -> &'static str {
                #data_type
            }

            fn note(self: &Self) -> String {
                format!(
                    "type: {}, kind: {}, instance: {:?}",
                    #name::type_name(),
                    #name::type_kind(),
                    self
                ).to_string()
            }
        }
    };

    code.into()
}
