use parquet::arrow::arrow_reader::ParquetRecordBatchReaderBuilder;
use arrow::array::StructArray;

use std::{env, fs::File, path::Path};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn print_attrs(s: &StructArray, prefix: &str) {
    if let Some(at) = s.column_by_name("Attrs") {
        let ns = ["Key", "Value", "ValueInt", "ValueDouble", "ValueBool", "ValueKVList", "ValueArray"];

        for a in at.data().child_data() {
            let a: StructArray = a.to_owned().into();

            for n in ns {
                if let Some(v) = a.column_by_name(n) {
                    if !v.is_empty() {
                        println!("{}/Attrs/{}: {:?}", prefix, n, v);
                    }
                }
            }
        }
    }
}

fn print_scope_span(s: &StructArray) {
    if let Some(il) = s.column_by_name("il") {
        let sc: StructArray = il.data().clone().into();

        for n in ["Name", "Version"] {
            if let Some(v) = sc.column_by_name(n) {
                println!("ScopeSpan/Scope/{}: {:?}", n, v);
            }
        }
    }

    if let Some(sp) = s.column_by_name("Spans") {
        for p in sp.data().child_data() {
            let p: StructArray = p.clone().into();

            for n in ["ID", "Name", "Kind", "ParentSpanID", "StartUnixNanos", "EndUnixNanos"] {
                if let Some(v) = p.column_by_name(n) {
                    println!("Spans/Span/{}: {:?}", n, v);
                }
            }

            print_attrs(&p, "Spans/Span");
        }
    }
}

fn main() -> Result<()> {
    let file_name = env::args().skip(1).next().unwrap();

    let path = Path::new(&file_name);
    let file = File::open(path)?;

    let reader = ParquetRecordBatchReaderBuilder::try_new(file)?
        .build()?;

    for r in reader {
        let b = r?;

        if let Some(t) = b.column_by_name("TraceIDText") {
            println!("TraceID: {:?}", t);
        }

        if let Some(r) = b.column_by_name("RootServiceName") {
            println!("RootServiceName: {:?}", r);
        }

        if let Some(rs) = b.column_by_name("rs") {
            for c in rs.data().child_data() {
                let st: StructArray = c.to_owned().into();

                if let Some(r) = st.column_by_name("Resource") {
                    let r: StructArray = r.data().to_owned().into();

                    if let Some(s) = r.column_by_name("ServiceName") {
                        println!("Resource/ServiceName: {:?}", s);  
                    }

                    print_attrs(&r, "Resource");
                }

                if let Some(ls) = st.column_by_name("ils") {
                    for l in ls.data().child_data() {
                        let s: StructArray = l.to_owned().into();

                        print_scope_span(&s);
                    }
                }
            }
        }
    }

    Ok(())
}
