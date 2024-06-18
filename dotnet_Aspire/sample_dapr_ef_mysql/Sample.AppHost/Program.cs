var builder = DistributedApplication.CreateBuilder(args);

var itemdb = builder.AddMySql("mysql1")
    .AddDatabase("itemdb");

var itemservice = builder.AddProject<Projects.Sample_ItemService>("itemservice")
    .WithReference(itemdb)
    .WithDaprSidecar();

var cartservice = builder.AddProject<Projects.Sample_CartService>("cartservice")
    .WithReference(itemservice)
    .WithDaprSidecar();

builder.Build().Run();
