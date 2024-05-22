using Grpc.Net.Client;

var channel = GrpcChannel.ForAddress("http://localhost:5001");

var client = new Process.ProcessClient(channel);

var res = client.Exec(new Input { Command = "test" });

Console.WriteLine($"{res}");
