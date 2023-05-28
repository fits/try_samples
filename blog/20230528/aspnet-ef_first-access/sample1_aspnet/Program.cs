var builder = WebApplication.CreateBuilder(args);

var app = builder.Build();

app.Map("/", () => "aspnet-" + DateTimeOffset.UtcNow.ToUnixTimeMilliseconds());

app.Run();
