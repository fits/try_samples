var builder = DistributedApplication.CreateBuilder(args);

var itemdb = builder.AddMySql("mysql1")
    .AddDatabase("itemdb");

builder.AddProject<Projects.Sample_ItemService>("itemservice")
    .WithReference(itemdb);

builder.Build().Run();
