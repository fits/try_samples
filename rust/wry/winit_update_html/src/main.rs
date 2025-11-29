use serde::Serialize;
use tera::{Context, Tera};
use winit::{
    application::ApplicationHandler,
    event::WindowEvent,
    event_loop::EventLoop,
    window::{Window, WindowAttributes},
};
use wry::{
    Rect, WebViewBuilder,
    dpi::{LogicalPosition, LogicalSize, PhysicalSize},
};

static TEMPLATE: &str = "
<html>
<body>
    <h1>Window Size</h1>
    <p>
        <h2>PhysicalSize</h2>
        <div>width={{ state.physical_size.width }}, height={{ state.physical_size.height }}</div>
    </p>
    <p>
        <h2>LogicalSize:</h2>
        <div>width={{ state.logical_size.width }}, height={{ state.logical_size.height }}</div>
    </p>
</body>
</html>
";

#[derive(Debug, Clone, Serialize)]
struct State {
    physical_size: Size,
    logical_size: Size,
}

#[derive(Debug, Clone, Serialize)]
struct Size {
    width: u32,
    height: u32,
}

impl From<PhysicalSize<u32>> for Size {
    fn from(value: PhysicalSize<u32>) -> Self {
        Self {
            width: value.width,
            height: value.height,
        }
    }
}

impl From<LogicalSize<u32>> for Size {
    fn from(value: LogicalSize<u32>) -> Self {
        Self {
            width: value.width,
            height: value.height,
        }
    }
}

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[derive(Default)]
struct App {
    window: Option<Window>,
    webview: Option<wry::WebView>,
    tera: Tera,
}

impl ApplicationHandler for App {
    fn resumed(&mut self, event_loop: &winit::event_loop::ActiveEventLoop) {
        let attrs = WindowAttributes::default().with_title("webview test");
        let window = event_loop.create_window(attrs);

        if let Ok(w) = window {
            let webview = WebViewBuilder::new().build_as_child(&w);

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

                        let v_pos = LogicalPosition::new(10, 10);
                        let v_size = LogicalSize::new(l_size.width * 3 / 4, l_size.height * 3 / 4);

                        let _ = view.set_bounds(Rect {
                            position: v_pos.into(),
                            size: v_size.into(),
                        });

                        let mut ctx = Context::new();
                        ctx.insert(
                            "state",
                            &State {
                                physical_size: size.into(),
                                logical_size: l_size.into(),
                            },
                        );

                        let html = self.tera.render("size.html", &ctx);

                        if let Ok(html) = html {
                            let _ = view.load_html(&html);
                        }
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
    let mut tera = Tera::default();
    tera.autoescape_on(vec![".html"]);

    tera.add_raw_template("size.html", TEMPLATE)?;

    let event_loop = EventLoop::new()?;

    let mut app = App {
        window: None,
        webview: None,
        tera,
    };

    event_loop.run_app(&mut app)?;

    Ok(())
}
