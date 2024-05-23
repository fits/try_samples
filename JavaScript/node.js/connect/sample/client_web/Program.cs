using Grpc.Net.Client;
using Grpc.Net.Client.Web;

var httpHandler = new HttpClientHandler();
httpHandler.ServerCertificateCustomValidationCallback = HttpClientHandler.DangerousAcceptAnyServerCertificateValidator;

var channel = GrpcChannel.ForAddress(
    "https://localhost:5001", 
    new GrpcChannelOptions { HttpHandler = new GrpcWebHandler(httpHandler) }
);

var client = new Process.ProcessClient(channel);

var res = client.Exec(new Input { Command = "test" });

Console.WriteLine($"{res}");
