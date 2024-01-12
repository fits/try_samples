use handlebars::Handlebars;
use serde_json::json;
use std::io::{self, Read};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let tpl = r#"<!DOCTYPE html>
<html>
<head>
{{#if enableMermaid}}
<script src="https://unpkg.com/mermaid/dist/mermaid.min.js"></script>
{{/if}}
</head>
<body>
{{{ content }}}
{{#if enableMermaid}}
<script>
    mermaid.initialize({ startOnLoad: false });    
    mermaid.run({ querySelector: '.language-mermaid' });
</script>
{{/if}}
</body>
</html>
"#;

    let mut buf = String::new();
    io::stdin().read_to_string(&mut buf)?;

    let has_mermaid = buf.contains("```mermaid");

    let parser = pulldown_cmark::Parser::new(&buf);

    let mut content = String::new();

    pulldown_cmark::html::push_html(&mut content, parser);

    let h = Handlebars::new();

    let res = h.render_template(
        tpl,
        &json!({"content": content, "enableMermaid": has_mermaid}),
    )?;

    print!("{}", res);

    Ok(())
}
