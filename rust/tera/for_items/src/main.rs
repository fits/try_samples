use serde::Serialize;
use tera::{Context, Tera};

static TEMPLATE: &str = "
<ul>
{% for item in items %}
    <li>name={{ item.name }}, price={{ item.price }}</li>
{% endfor %}
</ul>
";

#[derive(Debug, Clone, Serialize)]
struct Item {
    name: String,
    price: u32,
}

fn main() -> std::result::Result<(), Box<dyn std::error::Error>> {
    let mut tera = Tera::default();
    tera.autoescape_on(vec![".html"]);

    tera.add_raw_template("item.html", TEMPLATE)?;

    let items = vec![
        Item {
            name: "item1".to_string(),
            price: 110,
        },
        Item {
            name: "  item-2   ".to_string(),
            price: 2220,
        },
        Item {
            name: "".to_string(),
            price: 3000,
        },
        Item {
            name: "<item-4>".to_string(),
            price: 4400,
        },
    ];

    let mut ctx = Context::new();
    ctx.insert("items", &items);

    let res = tera.render("item.html", &ctx)?;

    print!("{}", res);

    Ok(())
}
