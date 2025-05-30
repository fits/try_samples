using System.Text.Json;
using AutoMapper;

var config = new MapperConfiguration(cfg =>
{
    cfg.CreateMap<Data1, Data2>();
    cfg.CreateMap<Data1, Data3>();
    cfg.CreateMap<Data1, Data4>();

    cfg.CreateMap<Data2, Data1>();
    cfg.CreateMap<Data2, Data3>();

    cfg.CreateMap<Data3, Data4>();
    cfg.CreateMap<Data3, Data5>();

    cfg.CreateMap<Data4, Data3>();

    cfg.CreateMap<Data5, Data1>();
    cfg.CreateMap<Data5, Data3>();

    cfg.CreateMap<Data6, Data3>();
});

var mapper = config.CreateMapper();

var d1 = new Data1("data1", DateTime.Now);
Console.WriteLine($"Data1: {d1}");

var d2 = mapper.Map<Data2>(d1);
Console.WriteLine($"Data1 -> Data2: {d2}");

var d3 = mapper.Map<Data3>(d1);
Console.WriteLine($"Data1 -> Data3: id={d3.Id}, createdAt={d3.CreatedAt}, json={JsonSerializer.Serialize(d3)}");

var d4 = mapper.Map<Data4>(d1);
Console.WriteLine($"Data1 -> Data4: {d4}");

var d5 = mapper.Map<Data1>(d2);
Console.WriteLine($"Data1 -> Data2 -> Data1: {d5}");

Console.WriteLine("-----");

var d6 = new Data2("data2", "2025/05/30");
Console.WriteLine($"Data2: {d6}");

var d7 = mapper.Map<Data3>(d6);
Console.WriteLine($"Data2 -> Data3: {JsonSerializer.Serialize(d7)}");

var d8 = mapper.Map<Data4>(d7);
Console.WriteLine($"Data2 -> Data3 -> Data4: {d8}");

var d9 = mapper.Map<Data3>(d8);
Console.WriteLine($"Data2 -> Data3 -> Data4 -> Data3: {JsonSerializer.Serialize(d9)}");

var d10 = mapper.Map<Data5>(d7);
Console.WriteLine($"Data2 -> Data3 -> Data5: {d10}");

var d11 = mapper.Map<Data3>(d10);
Console.WriteLine($"Data2 -> Data3 -> Data5 -> Data3: {JsonSerializer.Serialize(d11)}");

Console.WriteLine("-----");

var d12 = new Data5(Guid.NewGuid());
Console.WriteLine($"Data5: {d12}");

var d13 = mapper.Map<Data3>(d12);
Console.WriteLine($"Data5 -> Data3: {JsonSerializer.Serialize(d13)}");

Console.WriteLine("-----");

var d14 = new Data6("data6", "");
Console.WriteLine($"Data6: {d14}");

var d15 = mapper.Map<Data3>(d14);
Console.WriteLine($"Data6 -> Data3: {JsonSerializer.Serialize(d15)}");


record Data1(string name, DateTime createdAt);
record Data2(object name, string createdAt);

class Data3
{
    public required string Id { get; set; }
    public DateTime CreatedAt { get; set; }
}

record Data4(string dateTime = "", int id = 0, object? name = null);

record Data5(Guid id, object? createdAt = null);

record Data6(string name, string date);
