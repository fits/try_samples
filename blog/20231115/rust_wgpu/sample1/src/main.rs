type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    env_logger::init();

    let w = 320;
    let h = 240;

    let instance = wgpu::Instance::default();

    let adapter = instance
        .request_adapter(&wgpu::RequestAdapterOptions::default())
        .await
        .ok_or("notfound adapter")?;

    println!("adapter: {:?}", adapter.get_info());

    let (device, queue) = adapter
        .request_device(&wgpu::DeviceDescriptor::default(), None)
        .await?;

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

    let output_buf = device.create_buffer(&wgpu::BufferDescriptor {
        label: None,
        size: (w * h * 4) as u64,
        usage: wgpu::BufferUsages::MAP_READ | wgpu::BufferUsages::COPY_DST,
        mapped_at_creation: false,
    });

    let texture_view = texture.create_view(&wgpu::TextureViewDescriptor::default());

    let mut encoder = device.create_command_encoder(&wgpu::CommandEncoderDescriptor::default());

    {
        encoder.begin_render_pass(&wgpu::RenderPassDescriptor {
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
    }

    encoder.copy_texture_to_buffer(
        wgpu::ImageCopyTexture {
            texture: &texture,
            mip_level: 0,
            origin: wgpu::Origin3d::ZERO,
            aspect: wgpu::TextureAspect::All,
        },
        wgpu::ImageCopyBuffer {
            buffer: &output_buf,
            layout: wgpu::ImageDataLayout {
                offset: 0,
                bytes_per_row: Some(w * 4),
                rows_per_image: None,
            },
        },
        wgpu::Extent3d {
            width: w,
            height: h,
            depth_or_array_layers: 1,
        },
    );

    queue.submit(Some(encoder.finish()));

    let slice = output_buf.slice(..);
    slice.map_async(wgpu::MapMode::Read, |_| {});

    device.poll(wgpu::MaintainBase::Wait);

    image::save_buffer(
        "output.png",
        &slice.get_mapped_range().to_vec(),
        w,
        h,
        image::ColorType::Rgba8,
    )?;

    Ok(())
}
