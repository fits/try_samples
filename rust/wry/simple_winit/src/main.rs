use winit::{
    application::ApplicationHandler,
    event::WindowEvent,
    event_loop::EventLoop,
    window::{Window, WindowAttributes},
};
use wry::{
    Rect, WebViewBuilder,
    dpi::{LogicalPosition, LogicalSize},
};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[derive(Default)]
struct App {
    window: Option<Window>,
    webview: Option<wry::WebView>,
}

impl ApplicationHandler for App {
    fn resumed(&mut self, event_loop: &winit::event_loop::ActiveEventLoop) {
        let attrs = WindowAttributes::default().with_title("webview test");
        let window = event_loop.create_window(attrs);

        if let Ok(w) = window {
            let html = "
                <html>
                    <body>
                        <h1>WebView Test</h1>
                        <p>
                            <div>check1</div>
                        </p>
                    </body>
                </html>
            ";

            let webview = WebViewBuilder::new()
                .with_html(html)
                .with_bounds(Rect {
                    position: LogicalPosition::new(10, 10).into(),
                    size: LogicalSize::new(480, 360).into(),
                })
                .build_as_child(&w);

            match webview {
                Ok(v) => {
                    self.window = Some(w);
                    self.webview = Some(v);
                }
                Err(e) => println!("{:?}", e),
            }
        }
    }

    fn window_event(
        &mut self,
        event_loop: &winit::event_loop::ActiveEventLoop,
        _window_id: winit::window::WindowId,
        event: winit::event::WindowEvent,
    ) {
        if let Some(_w) = &self.window {
            match event {
                WindowEvent::CloseRequested => {
                    event_loop.exit();
                }
                _ => {}
            }
        } else {
            println!("failed create window and webview");
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
