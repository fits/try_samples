use winit::application::ApplicationHandler;
use winit::event::WindowEvent;
use winit::event_loop::EventLoop;
use winit::window::{Window, WindowAttributes};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[derive(Default)]
struct App(Option<Window>);

impl ApplicationHandler for App {
    fn resumed(&mut self, event_loop: &winit::event_loop::ActiveEventLoop) {
        let attrs = WindowAttributes::default().with_title("sample");

        self.0 = event_loop.create_window(attrs).ok();
    }

    fn window_event(
        &mut self,
        event_loop: &winit::event_loop::ActiveEventLoop,
        _window_id: winit::window::WindowId,
        event: WindowEvent,
    ) {
        if let Some(_window) = &self.0 {
            match event {
                WindowEvent::CloseRequested => {
                    println!("close requested");
                    event_loop.exit();
                }
                _ => {}
            }
        } else {
            println!("failed create_window");
            event_loop.exit();
        }
    }
}

fn main() -> Result<()> {
    let event_loop = EventLoop::new()?;

    let mut app = App::default();

    event_loop.run_app(&mut app)?;

    Ok(())
}
