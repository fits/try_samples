use winit::{event::*, event_loop::EventLoop, window::WindowBuilder};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

fn main() -> Result<()> {
    let event_loop = EventLoop::new()?;

    let window = WindowBuilder::new()
        .with_title("sample")
        .build(&event_loop)?;

    event_loop.run(move |ev, trg| match ev {
        Event::WindowEvent { window_id, event } if window_id == window.id() => match event {
            WindowEvent::CloseRequested => trg.exit(),
            _ => {}
        },
        _ => {}
    })?;

    Ok(())
}
