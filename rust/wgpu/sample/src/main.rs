type Result<T> = std::result::Result<T, Box<dyn std::error::Error>>;

#[tokio::main]
async fn main() -> Result<()> {
    env_logger::init();

    let instance = wgpu::Instance::default();

    let opts = wgpu::RequestAdapterOptions::default();
    let adapter = instance
        .request_adapter(&opts)
        .await
        .ok_or("notfound adapter")?;

    println!("adapter: {:?}", adapter.get_info());

    let desc = wgpu::DeviceDescriptor::default();
    let (device, queue) = adapter.request_device(&desc, None).await?;

    println!(
        "deviece: features={:?}, limits={:?}",
        device.features(),
        device.limits()
    );

    println!("queue: timestamp-period={}", queue.get_timestamp_period());

    Ok(())
}
