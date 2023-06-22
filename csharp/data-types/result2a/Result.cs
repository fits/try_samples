using System.Text.Json;
using System.Text.Json.Serialization;

public abstract record Result<T, E>
{
    public record OkResult(T Value) : Result<T, E> {}
    public record ErrResult(E Error) : Result<T, E> {}

    public static OkResult Ok(T value) => new OkResult(value);
    public static ErrResult Err(E error) => new ErrResult(error);

    public static string ToJson(Result<T, E> result)
    {
        return JsonSerializer.Serialize(
            result, 
            new JsonSerializerOptions
            {
                Converters = { new ResultConverter<T, E>() },
            }
        );
    }

    public static Result<T, E> FromJson(string json)
    {
        return JsonSerializer.Deserialize<Result<T, E>>(
            json, 
            new JsonSerializerOptions
            {
                Converters = { new ResultConverter<T, E>() },
            }
        )!;
    }
}

public static class ResultExtend
{
    public static bool IsOk<T, E>(this Result<T, E> target)
    {
        switch (target)
        {
            case Result<T, E>.OkResult r:
                return true;
            case Result<T, E>.ErrResult r:
                return false;
            default:
                throw new ArgumentException();
        }        
    }

    public static bool IsErr<T, E>(this Result<T, E> target) => !IsOk(target);

    public static T? Value<T, E>(this Result<T, E> target)
    {
        switch (target)
        {
            case Result<T, E>.OkResult r:
                return r.Value;
        }

        return default;    
    }

    public static E? Error<T, E>(this Result<T, E> target)
    {
        switch (target)
        {
            case Result<T, E>.ErrResult r:
                return r.Error;
        }

        return default;    
    }
}

class ResultConverter<T, E> : JsonConverter<Result<T, E>>
{
    public override Result<T, E> Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions opts)
    {
        using (var doc = JsonDocument.ParseValue(ref reader))
        {
            try {
                doc.RootElement.GetProperty("Value");
                return doc.Deserialize<Result<T, E>.OkResult>(opts)!;

            } catch(KeyNotFoundException) {
                return doc.Deserialize<Result<T, E>.ErrResult>(opts)!;
            }
        }
    }

    public override void Write(Utf8JsonWriter writer, Result<T, E> data, JsonSerializerOptions opts)
    {
        switch (data)
        {
            case Result<T, E>.OkResult r:
                JsonSerializer.Serialize(writer, r, opts);
                break;
            case Result<T, E>.ErrResult r:
                JsonSerializer.Serialize(writer, r, opts);
                break;
        }
    }
}