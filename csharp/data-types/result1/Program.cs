
var v1 = Result<int, string>.Ok(123);
Console.WriteLine(v1);
Console.WriteLine("is ok: {0}", v1.IsOk());

Console.WriteLine(v1 == Result<int, string>.Ok(123)); // True
Console.WriteLine(v1 == Result<int, string>.Ok(456)); // False

// Console.WriteLine(v1 == Result<int, bool>.Ok(123)); // compile error

var e1 = Result<int, string>.Err("failed");
Console.WriteLine(e1);
Console.WriteLine("is ok: {0}", e1.IsOk());

Console.WriteLine("-----");

var v2 = Result2<int, string>.Ok(123);
Console.WriteLine(v2);

Console.WriteLine(
    "is ok: {0}, is err: {1}, value: {2}, error: {3}", 
    v2.IsOk(), v2.IsErr(), v2.Value(), v2.Error()
);

Console.WriteLine(v2 == Result2<int, string>.Ok(123)); // True
Console.WriteLine(v2 == Result2<int, string>.Ok(456)); // False

var e2 = Result2<int, string>.Err("failed");
Console.WriteLine(e2);

Console.WriteLine(
    "is ok: {0}, is err: {1}, value: {2}, error: {3}", 
    e2.IsOk(), e2.IsErr(), e2.Value(), e2.Error()
);


public interface IResult
{
    bool IsOk();
}

public abstract record Result<T, E>
{
    public record OkResult(T Value) : Result<T, E>, IResult {
        public bool IsOk() => true;
    }
    public record ErrResult(E Error) : Result<T, E>, IResult {
        public bool IsOk() => false;
    }

    public static OkResult Ok(T value) => new OkResult(value);
    public static ErrResult Err(E error) => new ErrResult(error);
}

public abstract record Result2<T, E>
{
    public record OkResult(T Value) : Result2<T, E> {}
    public record ErrResult(E Error) : Result2<T, E> {}

    public static OkResult Ok(T value) => new OkResult(value);
    public static ErrResult Err(E error) => new ErrResult(error);
}

public static class Result2Extend
{
    public static bool IsOk<T, E>(this Result2<T, E> target)
    {
        switch (target)
        {
            case Result2<T, E>.OkResult r:
                return true;
            case Result2<T, E>.ErrResult r:
                return false;
            default:
                throw new ArgumentException();
        }        
    }

    public static bool IsErr<T, E>(this Result2<T, E> target) => !IsOk(target);

    public static T? Value<T, E>(this Result2<T, E> target)
    {
        switch (target)
        {
            case Result2<T, E>.OkResult r:
                return r.Value;
        }

        return default;    
    }

    public static E? Error<T, E>(this Result2<T, E> target)
    {
        switch (target)
        {
            case Result2<T, E>.ErrResult r:
                return r.Error;
        }

        return default;    
    }
}