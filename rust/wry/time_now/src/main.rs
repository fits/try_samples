use std::time::{Duration, Instant};

use winit::{
    application::ApplicationHandler,
    event::WindowEvent,
    event_loop::{ControlFlow, EventLoop},
    window::{Window, WindowAttributes},
};
use wry::{
    Rect, WebViewBuilder,
    dpi::{LogicalPosition, LogicalSize},
};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

struct App {
    window: Option<Window>,
    webview: Option<wry::WebView>,
    instant: Instant,
}

const REDRAW_INTERVAL: Duration = Duration::from_millis(500);

const HTML: &'static str = r#"
<html>
<body>
    <div id="time"></div>
    <script>
        function drawTime() {
            const now = new Date().toISOString()

            document.getElementById('time').textContent = now

            return now
        }

        drawTime()
    </script>
</body>
</html>
"#;

impl App {
    fn new() -> Self {
        Self {
            window: None,
            webview: None,
            instant: Instant::now(),
        }
    }
}

impl ApplicationHandler for App {
    fn resumed(&mut self, event_loop: &winit::event_loop::ActiveEventLoop) {
        let attrs = WindowAttributes::default().with_title("webview test");

        self.window = event_loop.create_window(attrs).ok();

        self.webview = self.window.as_ref().and_then(|w| {
            WebViewBuilder::new()
                .with_html(HTML)
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
        if let Some(window) = &self.window {
            match event {
                WindowEvent::CloseRequested => {
                    event_loop.exit();
                }
                WindowEvent::Resized(size) => {
                    if let Some(view) = &self.webview {
                        let l_size = size.to_logical::<u32>(window.scale_factor());

                        println!("new physical size: {:?}, logical size: {:?}", size, l_size);

                        let v_pos = LogicalPosition::new(10, 10);
                        let v_size = LogicalSize::new(l_size.width * 3 / 4, l_size.height * 3 / 4);

                        let _ = view.set_bounds(Rect {
                            position: v_pos.into(),
                            size: v_size.into(),
                        });
                    }
                }
                WindowEvent::RedrawRequested => {
                    if let Some(view) = &self.webview {
                        let now = Instant::now();

                        if now.duration_since(self.instant) >= REDRAW_INTERVAL {
                            let _ = view.evaluate_script_with_callback("drawTime()", |r| {
                                println!("returned drawTime: {r:}");
                            });

                            self.instant = now;
                        }

                        window.request_redraw();
                    }
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

    event_loop.set_control_flow(ControlFlow::Poll);

    event_loop.run_app(&mut App::new())?;

    Ok(())
}
