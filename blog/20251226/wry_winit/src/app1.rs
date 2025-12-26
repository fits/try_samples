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

const HTML: &'static str = r#"
<html>
<body>
    <div id="time"></div>
    <script>
        const now = new Date().toISOString()
        document.getElementById('time').textContent = now
    </script>
</body>
</html>
"#;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[derive(Default)]
struct App {
    window: Option<Window>,
    webview: Option<wry::WebView>,
}

impl ApplicationHandler for App {
    fn resumed(&mut self, event_loop: &winit::event_loop::ActiveEventLoop) {
        let attrs = WindowAttributes::default().with_title("webview app1");
        self.window = event_loop.create_window(attrs).ok();

        self.webview = self.window.as_ref().and_then(|w| {
            WebViewBuilder::new()
                .with_html(HTML)
                .with_bounds(Rect {
                    position: LogicalPosition::new(10, 10).into(),
                    size: LogicalSize::new(500, 300).into(),
                })
                .build_as_child(&w)
                .ok()
        });
    }

    fn window_event(
        &mut self,
        event_loop: &winit::event_loop::ActiveEventLoop,
        _window_id: winit::window::WindowId,
        event: winit::event::WindowEvent,
    ) {
        if let Some(_window) = &self.window {
            match event {
                WindowEvent::CloseRequested => {
                    event_loop.exit();
                }
                _ => {}
            }
        } else {
            println!("failed to create webview");
            event_loop.exit();
        }
    }
}

fn main() -> Result<()> {
    let event_loop = EventLoop::new()?;

    event_loop.run_app(&mut App::default())?;

    Ok(())
}
