use headless_chrome::Browser;

use std::{env, fs};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let file = env::args().skip(1).next().ok_or("file path")?;
    let file_path = fs::canonicalize(file)?;

    let url = format!("file://{}", file_path.to_str().ok_or("err")?);

    println!("{}", url);

    let browser = Browser::default()?;
    let tab = browser.new_tab()?;

    let data = tab
        .navigate_to(&url)?
        .wait_until_navigated()?
        .print_to_pdf(None)?;

    fs::write("output.pdf", data)?;

    tab.close(true)?;

    Ok(())
}
