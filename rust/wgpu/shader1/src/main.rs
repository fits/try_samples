use std::borrow::Cow;
use wgpu::util::DeviceExt;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    env_logger::init();

    let w = 320;
    let h = 240;

    let vs: Vec<f32> = vec![
        -0.7, -0.5, 
        0.5, -0.2, 
        0.0, 0.6, 
        -0.7, -0.5, 
        0.8, -0.7, 
        0.9, -0.4,
    ];

    let instance = wgpu::Instance::default();

    let opts = wgpu::RequestAdapterOptions::default();
    let adapter = instance
        .request_adapter(&opts)
        .await
        .ok_or("notfound adapter")?;

    let desc = wgpu::DeviceDescriptor::default();
    let (device, queue) = adapter.request_device(&desc, None).await?;

    let shader = device.create_shader_module(wgpu::ShaderModuleDescriptor {
        label: None,
        source: wgpu::ShaderSource::Wgsl(Cow::Borrowed(include_str!("shader.wgsl"))),
    });

    let pipeline_layout = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
        label: None,
        bind_group_layouts: &[],
        push_constant_ranges: &[],
    });

    let vs_buf_layout = wgpu::VertexBufferLayout {
        array_stride: 8,
        attributes: &[wgpu::VertexAttribute {
            format: wgpu::VertexFormat::Float32x2,
            offset: 0,
            shader_location: 0,
        }],
        step_mode: wgpu::VertexStepMode::Vertex,
    };

    let pipeline = device.create_render_pipeline(&wgpu::RenderPipelineDescriptor {
        label: None,
        layout: Some(&pipeline_layout),
        vertex: wgpu::VertexState {
            module: &shader,
            entry_point: "vs_main",
            buffers: &[vs_buf_layout],
        },
        fragment: Some(wgpu::FragmentState {
            module: &shader,
            entry_point: "fs_main",
            targets: &[Some(wgpu::TextureFormat::Rgba8UnormSrgb.into())],
        }),
        primitive: wgpu::PrimitiveState::default(),
        depth_stencil: None,
        multisample: wgpu::MultisampleState::default(),
        multiview: None,
    });

    let texture = device.create_texture(&wgpu::TextureDescriptor {
        label: None,
        size: wgpu::Extent3d {
            width: w,
            height: h,
            depth_or_array_layers: 1,
        },
        mip_level_count: 1,
        sample_count: 1,
        dimension: wgpu::TextureDimension::D2,
        format: wgpu::TextureFormat::Rgba8UnormSrgb,
        usage: wgpu::TextureUsages::RENDER_ATTACHMENT | wgpu::TextureUsages::COPY_SRC,
        view_formats: &[wgpu::TextureFormat::Rgba8UnormSrgb],
    });

    let vertex_buf = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
        label: None,
        contents: bytemuck::cast_slice(&vs),
        usage: wgpu::BufferUsages::VERTEX,
    });

    let output_buf = device.create_buffer(&wgpu::BufferDescriptor {
        label: None,
        size: (w * h * 4) as u64,
        usage: wgpu::BufferUsages::MAP_READ | wgpu::BufferUsages::COPY_DST,
        mapped_at_creation: false,
    });

    let texture_view = texture.create_view(&wgpu::TextureViewDescriptor::default());

    let mut encoder = device.create_command_encoder(&wgpu::CommandEncoderDescriptor::default());

    {
        let mut rpass = encoder.begin_render_pass(&wgpu::RenderPassDescriptor {
            label: None,
            color_attachments: &[Some(wgpu::RenderPassColorAttachment {
                view: &texture_view,
                resolve_target: None,
                ops: wgpu::Operations {
                    load: wgpu::LoadOp::Clear(wgpu::Color::BLUE),
                    store: wgpu::StoreOp::Store,
                },
            })],
            depth_stencil_attachment: None,
            timestamp_writes: None,
            occlusion_query_set: None,
        });

        rpass.set_pipeline(&pipeline);
        rpass.set_vertex_buffer(0, vertex_buf.slice(..));
        rpass.draw(0..(vs.len() as u32 / 2), 0..1);
    }

    let t = wgpu::ImageCopyTexture {
        texture: &texture,
        mip_level: 0,
        origin: wgpu::Origin3d::ZERO,
        aspect: wgpu::TextureAspect::All,
    };

    let b = wgpu::ImageCopyBuffer {
        buffer: &output_buf,
        layout: wgpu::ImageDataLayout {
            offset: 0,
            bytes_per_row: Some(w * 4),
            rows_per_image: Some(h),
        },
    };

    encoder.copy_texture_to_buffer(
        t,
        b,
        wgpu::Extent3d {
            width: w,
            height: h,
            depth_or_array_layers: 1,
        },
    );

    queue.submit(Some(encoder.finish()));

    let output_slice = output_buf.slice(..);

    output_slice.map_async(wgpu::MapMode::Read, |_| {});

    device.poll(wgpu::MaintainBase::Wait);

    let data = output_slice.get_mapped_range().to_vec();

    image::save_buffer("output.png", &data, w, h, image::ColorType::Rgba8)?;

    output_buf.unmap();

    Ok(())
}
