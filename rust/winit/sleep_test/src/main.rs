use std::thread;
use std::time::{Duration, Instant};

use winit::application::ApplicationHandler;
use winit::event::WindowEvent;
use winit::event_loop::{ControlFlow, EventLoop};
use winit::window::{Window, WindowAttributes};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

struct App(Option<Window>, Instant);

const SLEEP_TIME: Duration = Duration::from_millis(500);

impl ApplicationHandler for App {
    fn resumed(&mut self, event_loop: &winit::event_loop::ActiveEventLoop) {
        let attrs = WindowAttributes::default()
            .with_title("sleep test")
            .with_resizable(false);

        self.0 = event_loop.create_window(attrs).ok();
    }

    fn window_event(
        &mut self,
        event_loop: &winit::event_loop::ActiveEventLoop,
        _window_id: winit::window::WindowId,
        event: WindowEvent,
    ) {
        if let Some(window) = &self.0 {
            match event {
                WindowEvent::CloseRequested => {
                    println!("close requested");
                    event_loop.exit();
                }
                WindowEvent::RedrawRequested => {
                    println!("redraw requested: {:?}", self.1.elapsed());
                    window.request_redraw();
                }
                _ => {}
            }
        } else {
            println!("failed create_window");
            event_loop.exit();
        }
    }

    fn about_to_wait(&mut self, _event_loop: &winit::event_loop::ActiveEventLoop) {
        thread::sleep(SLEEP_TIME);
    }
}

fn main() -> Result<()> {
    let event_loop = EventLoop::new()?;

    event_loop.set_control_flow(ControlFlow::Poll);

    let mut app = App(None, Instant::now());

    event_loop.run_app(&mut app)?;

    Ok(())
}
