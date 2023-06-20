using System.Text.Json;

var d1 = Sample.CreateData("id-1");

Console.WriteLine(d1);

Console.WriteLine(
    "ok={0}, err={1}, value={2}, error={3}", 
    d1.IsOk(), d1.IsErr(), d1.Value(), d1.Error()
);

// {}
Console.WriteLine(JsonSerializer.Serialize(d1));
// {"Value":{"Id":"id-1"}}
Console.WriteLine(JsonSerializer.Serialize(d1 as Result<Data, Error>.OkResult));

var json1 = d1.ToJson();
Console.WriteLine(json1);
Console.WriteLine(Result<Data, Error>.FromJson(json1));

Console.WriteLine("-----");

var d2 = Sample.CreateData("2");

Console.WriteLine(d2);

Console.WriteLine(
    "ok={0}, err={1}, value={2}, error={3}", 
    d2.IsOk(), d2.IsErr(), d2.Value(), d2.Error()
);

// {}
Console.WriteLine(JsonSerializer.Serialize(d2));
// {"Error":{"Code":"e01"}}
Console.WriteLine(JsonSerializer.Serialize(d2 as Result<Data, Error>.ErrResult));

var json2 = d2.ToJson();
Console.WriteLine(json2);
Console.WriteLine(Result<Data, Error>.FromJson(json2));

record Data(string Id);
record Error(string Code);

static class Sample
{
    public static Result<Data, Error> CreateData(string id)
    {
        if (id.StartsWith("id-"))
        {
            return Result<Data, Error>.Ok(new Data(id));
        }

        return Result<Data, Error>.Err(new Error("e01"));
    }
}
