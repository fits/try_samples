
using Dapr;
using Dapr.Client;
using Dapr.Actors;
using Dapr.Actors.Client;

var builder = WebApplication.CreateBuilder(args);

var app = builder.Build();

var createProxy = (ActorId id) => ActorProxy.Create<ICartActor>(id, "CartActor");

app.MapPost("/create", () => {
    Console.WriteLine($"{DateTime.UtcNow.ToString("o")} - cartcall /create");
    return createProxy(ActorId.CreateRandom()).CreateAsync();
});

app.MapPost("/additem", (AddInput input) => {
    Console.WriteLine($"{DateTime.UtcNow.ToString("o")} - cartcall /additem : {input}");

    var proxy = createProxy(new ActorId(input.Id));
    return proxy.AddItemAsync(input.Item, input.Price, input.Qty);
});

app.MapPost("/get", (GetInput input) => {
    Console.WriteLine($"{DateTime.UtcNow.ToString("o")} - cartcall /get : {input}");

    return createProxy(new ActorId(input.Id)).GetAsync();
});

app.Run();

record GetInput(string Id);
record AddInput(string Id, string Item, int Price, int Qty);
