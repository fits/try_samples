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

            let webview = WebViewBuilder::new().with_html(html).build_as_child(&w);

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

                        println!("view size: {:?}", v_size);

                        let _ = view.set_bounds(Rect {
                            position: v_pos.into(),
                            size: v_size.into(),
                        });
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

    let mut app = App::default();

    event_loop.run_app(&mut app)?;

    Ok(())
}
