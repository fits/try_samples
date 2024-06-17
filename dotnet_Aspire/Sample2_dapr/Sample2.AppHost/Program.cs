using Aspire.Hosting.Dapr;

var builder = DistributedApplication.CreateBuilder(args);

var itemService = builder.AddProject<Projects.Sample2_ItemService>("itemservice")
    .WithDaprSidecar();

builder.AddProject<Projects.Sample2_DataInfoService>("datainfoservice")
    .WithReference(itemService)
    .WithDaprSidecar();

builder.Build().Run();
