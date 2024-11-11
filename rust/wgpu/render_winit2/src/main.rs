use std::borrow::Cow;
use wgpu::util::DeviceExt;
use wgpu::{Adapter, Device, Instance, Queue, RenderPipeline, Surface};
use winit::application::ApplicationHandler;
use winit::event::WindowEvent;
use winit::event_loop::EventLoop;
use winit::window::{Window, WindowAttributes};

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

struct App<'a> {
    instance: Instance,
    adapter: Adapter,
    device: Device,
    queue: Queue,
    points: Vec<f32>,
    render_context: Option<(Surface<'a>, RenderPipeline)>,
}

impl<'a> App<'a> {
    fn init(&mut self, window: Window) -> Result<()> {
        let size = window.inner_size();

        let surface = self.instance.create_surface(window)?;

        let config = surface
            .get_default_config(&self.adapter, size.width, size.height)
            .ok_or("failed surface get_default_config")?;

        surface.configure(&self.device, &config);

        let shader = self
            .device
            .create_shader_module(wgpu::ShaderModuleDescriptor {
                label: None,
                source: wgpu::ShaderSource::Wgsl(Cow::Borrowed(include_str!("shader.wgsl"))),
            });

        let pipeline_layout = self
            .device
            .create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
                label: None,
                bind_group_layouts: &[],
                push_constant_ranges: &[],
            });

        let capabilities = surface.get_capabilities(&self.adapter);

        let vs_buf_layout = wgpu::VertexBufferLayout {
            array_stride: 8,
            attributes: &[wgpu::VertexAttribute {
                format: wgpu::VertexFormat::Float32x2,
                offset: 0,
                shader_location: 0,
            }],
            step_mode: wgpu::VertexStepMode::Vertex,
        };

        let pipeline = self
            .device
            .create_render_pipeline(&wgpu::RenderPipelineDescriptor {
                label: None,
                layout: Some(&pipeline_layout),
                vertex: wgpu::VertexState {
                    module: &shader,
                    entry_point: Some("vs_main"),
                    buffers: &[vs_buf_layout],
                    compilation_options: Default::default(),
                },
                fragment: Some(wgpu::FragmentState {
                    module: &shader,
                    entry_point: Some("fs_main"),
                    targets: &[Some(capabilities.formats[0].into())],
                    compilation_options: Default::default(),
                }),
                primitive: wgpu::PrimitiveState::default(),
                depth_stencil: None,
                multisample: wgpu::MultisampleState::default(),
                multiview: None,
                cache: None,
            });

        self.render_context = Some((surface, pipeline));

        Ok(())
    }

    fn render(&self) -> Result<()> {
        if let Some((surface, pipeline)) = &self.render_context {
            let frame = surface.get_current_texture()?;

            let view = frame
                .texture
                .create_view(&wgpu::TextureViewDescriptor::default());

            let buffer = self
                .device
                .create_buffer_init(&wgpu::util::BufferInitDescriptor {
                    label: None,
                    contents: bytemuck::cast_slice(&self.points),
                    usage: wgpu::BufferUsages::VERTEX,
                });

            let mut encoder = self
                .device
                .create_command_encoder(&wgpu::CommandEncoderDescriptor::default());

            {
                let mut rpass = encoder.begin_render_pass(&wgpu::RenderPassDescriptor {
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

                rpass.set_pipeline(pipeline);
                rpass.set_vertex_buffer(0, buffer.slice(..));
                rpass.draw(0..(self.points.len() as u32 / 2), 0..1);
            }

            self.queue.submit(Some(encoder.finish()));

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
            .and_then(|w| self.init(w));

        if let Err(e) = res {
            println!("window ERROR: {e}");
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

#[tokio::main]
async fn main() -> Result<()> {
    let instance = wgpu::Instance::default();

    let opts = wgpu::RequestAdapterOptions::default();
    let adapter = instance
        .request_adapter(&opts)
        .await
        .ok_or("notfound adapter")?;

    let desc = wgpu::DeviceDescriptor::default();
    let (device, queue) = adapter.request_device(&desc, None).await?;

    let event_loop = EventLoop::new()?;

    let mut app = App {
        instance,
        adapter,
        device,
        queue,
        points: vec![
            -0.7, -0.5, 0.5, -0.2, 0.0, 0.6, -0.7, -0.5, 0.8, -0.7, 0.9, -0.4,
        ],
        render_context: None,
    };

    event_loop.run_app(&mut app)?;

    Ok(())
}
