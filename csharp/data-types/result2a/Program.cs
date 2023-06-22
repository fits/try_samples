var d1 = Sample.CreateData("id-1");

Console.WriteLine(d1);

Console.WriteLine(
    "ok={0}, err={1}, value={2}, error={3}", 
    d1.IsOk(), d1.IsErr(), d1.Value(), d1.Error()
);

var json1 = Result<Data, Error>.ToJson(d1);

Console.WriteLine(json1);
Console.WriteLine(Result<Data, Error>.FromJson(json1));

Console.WriteLine("-----");

var d2 = Sample.CreateData("2");

Console.WriteLine(d2);

Console.WriteLine(
    "ok={0}, err={1}, value={2}, error={3}", 
    d2.IsOk(), d2.IsErr(), d2.Value(), d2.Error()
);

var json2 = Result<Data, Error>.ToJson(d2);

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
