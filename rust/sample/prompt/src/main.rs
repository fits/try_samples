use std::io::{stdin, stdout, Write};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    loop {
        println!("# please input:");
        print!("> ");
    
        stdout().flush()?;
    
        let mut input = String::new();
        stdin().read_line(&mut input)?;
    
        if input.trim() == "q" {
            break
        }

        println!("{}", input);
    
    }

    Ok(())
}
