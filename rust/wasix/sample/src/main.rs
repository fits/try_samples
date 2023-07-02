type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    print_message("step1\n")?;

    unsafe {
        wasix::thread_sleep(3)?;
    }

    print_message("step2\n")?;

    Ok(())
}

fn print_message(msg: &str) -> Result<()> {
    let d = [wasix::Ciovec {
        buf: msg.as_ptr(),
        buf_len: msg.len(),
    }];

    unsafe {
        wasix::fd_write(1, &d)?;
    }

    Ok(())
}