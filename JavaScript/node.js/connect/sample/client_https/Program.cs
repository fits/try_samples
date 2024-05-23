using Grpc.Net.Client;

var httpHandler = new HttpClientHandler();
httpHandler.ServerCertificateCustomValidationCallback = HttpClientHandler.DangerousAcceptAnyServerCertificateValidator;

var channel = GrpcChannel.ForAddress(
    "https://localhost:5001", 
    new GrpcChannelOptions { HttpHandler = httpHandler }
);

var client = new Process.ProcessClient(channel);

var res = client.Exec(new Input { Command = "test" });

Console.WriteLine($"{res}");
