use futures::executor::block_on;
use wgpu::{Device, Queue, Surface};
use winit::application::ApplicationHandler;
use winit::event::WindowEvent;
use winit::event_loop::EventLoop;
use winit::window::{Window, WindowAttributes};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

struct App<'a> {
    renderer: Option<Renderer<'a>>,
}

struct Renderer<'a> {
    surface: Surface<'a>,
    device: Device,
    queue: Queue,
}

impl<'a> App<'a> {
    async fn init(&mut self, window: Window) -> Result<()> {
        let instance = wgpu::Instance::default();

        let size = window.inner_size();

        let surface = instance.create_surface(window)?;

        let opts = wgpu::RequestAdapterOptions::default();

        let adapter = instance
            .request_adapter(&opts)
            .await
            .ok_or("notfound adapter")?;

        let desc = wgpu::DeviceDescriptor::default();
        let (device, queue) = adapter.request_device(&desc, None).await?;

        let config = surface
            .get_default_config(&adapter, size.width, size.height)
            .ok_or("failed surface get_default_config")?;

        surface.configure(&device, &config);

        self.renderer = Some(Renderer {
            surface,
            device,
            queue,
        });

        Ok(())
    }

    fn render(&self) -> Result<()> {
        if let Some(r) = &self.renderer {
            let frame = r.surface.get_current_texture()?;

            let view = frame
                .texture
                .create_view(&wgpu::TextureViewDescriptor::default());

            let mut encoder = r
                .device
                .create_command_encoder(&wgpu::CommandEncoderDescriptor::default());

            {
                encoder.begin_render_pass(&wgpu::RenderPassDescriptor {
                    label: None,
                    color_attachments: &[Some(wgpu::RenderPassColorAttachment {
                        view: &view,
                        resolve_target: None,
                        ops: wgpu::Operations {
                            load: wgpu::LoadOp::Clear(wgpu::Color::GREEN),
                            store: wgpu::StoreOp::Store,
                        },
                    })],
                    depth_stencil_attachment: None,
                    timestamp_writes: None,
                    occlusion_query_set: None,
                });
            }

            r.queue.submit(Some(encoder.finish()));

            frame.present();
        }

        Ok(())
    }
}

impl<'a> ApplicationHandler for App<'a> {
    fn resumed(&mut self, event_loop: &winit::event_loop::ActiveEventLoop) {
        let attrs = WindowAttributes::default().with_title("sample");

        let res = event_loop
            .create_window(attrs)
            .map_err(|e| e.into())
            .and_then(|w| block_on(self.init(w)));

        if let Err(e) = res {
            println!("ERROR: {e}");
            event_loop.exit();
        }
    }

    fn window_event(
        &mut self,
        event_loop: &winit::event_loop::ActiveEventLoop,
        _window_id: winit::window::WindowId,
        event: WindowEvent,
    ) {
        match event {
            WindowEvent::CloseRequested => {
                println!("close requested");
                event_loop.exit();
            }
            WindowEvent::RedrawRequested => {
                if let Err(e) = self.render() {
                    println!("render ERROR: {e}");
                    event_loop.exit();
                }
            }
            _ => {}
        }
    }
}

fn main() -> Result<()> {
    let event_loop = EventLoop::new()?;

    let mut app = App { renderer: None };

    event_loop.run_app(&mut app)?;

    Ok(())
}
