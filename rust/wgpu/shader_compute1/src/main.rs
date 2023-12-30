use std::borrow::Cow;
use wgpu::util::DeviceExt;

type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    env_logger::init();

    let vs: Vec<u32> = vec![1, 2, 3, 4, 5, 6, 7, 8];

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

    let pipeline = device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
        label: None,
        layout: None,
        module: &shader,
        entry_point: "main",
    });

    let storage_buf = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
        label: None,
        contents: bytemuck::cast_slice(&vs),
        usage: wgpu::BufferUsages::STORAGE
            | wgpu::BufferUsages::COPY_DST
            | wgpu::BufferUsages::COPY_SRC,
    });

    let output_buf = device.create_buffer(&wgpu::BufferDescriptor {
        label: None,
        size: storage_buf.size(),
        usage: wgpu::BufferUsages::MAP_READ | wgpu::BufferUsages::COPY_DST,
        mapped_at_creation: false,
    });

    let bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
        label: None,
        layout: &pipeline.get_bind_group_layout(0),
        entries: &[wgpu::BindGroupEntry {
            binding: 0,
            resource: storage_buf.as_entire_binding(),
        }],
    });

    let mut encoder = device.create_command_encoder(&wgpu::CommandEncoderDescriptor::default());

    {
        let mut pass = encoder.begin_compute_pass(&wgpu::ComputePassDescriptor {
            label: None,
            timestamp_writes: None,
        });

        pass.set_pipeline(&pipeline);
        pass.set_bind_group(0, &bind_group, &[]);
        pass.dispatch_workgroups(vs.len() as u32, 1, 1);
    }

    encoder.copy_buffer_to_buffer(&storage_buf, 0, &output_buf, 0, storage_buf.size());

    queue.submit(Some(encoder.finish()));

    let output_slice = output_buf.slice(..);

    output_slice.map_async(wgpu::MapMode::Read, |_| {});

    device.poll(wgpu::MaintainBase::Wait);

    let data = output_slice.get_mapped_range();

    let res = bytemuck::cast_slice::<_, u32>(&data).to_vec();

    println!("{:?}", res);

    drop(data);
    output_buf.unmap();

    Ok(())
}
