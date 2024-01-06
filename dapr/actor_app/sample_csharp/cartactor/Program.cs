
var builder = WebApplication.CreateBuilder(args);

builder.Services.AddActors(opts =>
{
    opts.Actors.RegisterActor<CartActor>();
});

var app = builder.Build();

app.MapActorsHandlers();

app.Run();
