var builder = DistributedApplication.CreateBuilder(args);

var itemService = builder.AddProject<Projects.Sample1_ItemService>("itemservice");

builder.AddProject<Projects.Sample1_DataInfoService>("datainfoservice").WithReference(itemService);

builder.Build().Run();
