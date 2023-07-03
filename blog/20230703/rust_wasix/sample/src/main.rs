type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    print_message("step1\n")?;

    unsafe {
        // sleep 5sec
        wasix::thread_sleep(5 * 1000000000)?;
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
        wasix::fd_write(wasix::FD_STDOUT, &d)?;
    }

    Ok(())
}
