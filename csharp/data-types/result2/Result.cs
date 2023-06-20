using System.Text.Json;

public abstract record Result<T, E>
{
    public record OkResult(T Value) : Result<T, E> {}
    public record ErrResult(E Error) : Result<T, E> {}

    public static OkResult Ok(T value) => new OkResult(value);
    public static ErrResult Err(E error) => new ErrResult(error);

    public static Result<T, E>? FromJson(string json)
    {
        var doc = JsonDocument.Parse(json);

        try {
            doc.RootElement.GetProperty("Value");
            return doc.Deserialize<OkResult>();

        } catch(KeyNotFoundException) {
            return doc.Deserialize<ErrResult>();
        }
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

    public static string ToJson<T, E>(this Result<T, E> target)
    {
        switch (target)
        {
            case Result<T, E>.OkResult r:
                return JsonSerializer.Serialize(r);
            case Result<T, E>.ErrResult r:
                return JsonSerializer.Serialize(r);
            default:
                throw new ArgumentException();
        }
    }
}