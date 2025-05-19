using System.Net.Http.Headers;

var builder = WebApplication.CreateBuilder(args);

var itemApiUrl = builder.Configuration["ItemApiUrl"] ?? throw new Exception("ItemApiUrl is not set");

builder.Services.AddHttpClient("itemApi", client =>
{
    client.BaseAddress = new Uri(itemApiUrl);
});

builder.Services.AddHttpLogging(o => 
{
    o.CombineLogs = true;
});

var app = builder.Build();

app.UseMiddleware<ErrorHandleMiddleware>();
app.UseHttpLogging();

var rnd = new Random();

app.MapPost("/item", async (HttpContext ctx, IHttpClientFactory factory) =>
{
    var client = factory.CreateClient("itemApi");

    var content = new StreamContent(ctx.Request.Body);
    content.Headers.ContentType = new MediaTypeHeaderValue(ctx.Request.ContentType ?? "application/json");

    var res = await client.PostAsync("/create", content);

    ctx.Response.StatusCode = (int)res.StatusCode;
    await res.Content.CopyToAsync(ctx.Response.Body);
});

app.Run();
