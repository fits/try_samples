using System.Reflection;
using AsyncAdvice = System.Func<(System.Reflection.MethodInfo targetMethod, object?[]? args), System.Threading.Tasks.Task>;

var svc = new TestService();

var proxy = SyncProxy<ITestService>.CreateProxy(svc);

await proxy.ConnectAsync("t1-1");

var res = await proxy.CheckAsync("t1-2");
Console.WriteLine($"res={res}");

proxy.Disconnect("t1-3");

var proxy2 = AsyncProxy<ITestService>.CreateProxy(
    svc,
    async (x) =>
    {
        var (_, a) = x;

        Console.WriteLine($"** async before START: {string.Join(",", a ?? [])}");
        await Task.Delay(100);
        Console.WriteLine($"** async before END: {string.Join(",", a ?? [])}");
    },
    async (x) => 
    {
        var (_, a) = x;

        Console.WriteLine($"** async after START: {string.Join(",", a ?? [])}");
        await Task.Delay(100);
        Console.WriteLine($"** async after END: {string.Join(",", a ?? [])}");
    }
);

await proxy2.ConnectAsync("t2-1");

var res2 = await proxy2.CheckAsync("t2-2");
Console.WriteLine($"res={res2}");

proxy2.Disconnect("t2-3");


interface ITestService
{
    Task ConnectAsync(string id);
    Task<bool> CheckAsync(string id);
    void Disconnect(string id);
}

class TestService : ITestService
{
    public async Task ConnectAsync(string id)
    {
        Console.WriteLine($"start connect: {id}");

        await Task.Delay(50);

        Console.WriteLine($"end connect: {id}");
    }

    public async Task<bool> CheckAsync(string id)
    {
        Console.WriteLine($"start check: {id}");

        await Task.Delay(50);

        Console.WriteLine($"end check: {id}");

        return true;
    }

    public void Disconnect(string id)
    {
        Console.WriteLine($"disconnect: {id}");
    }
}

class SyncProxy<T> : DispatchProxy
{
    private T? _target;

    public static T CreateProxy(T target)
    {
        var proxy = Create<T, SyncProxy<T>>();

        if (proxy is SyncProxy<T> p)
        {
            p._target = target;
        }

        return proxy;
    }

    protected override object? Invoke(MethodInfo? targetMethod, object?[]? args)
    {
        Console.WriteLine($"* sync before");

        var res = targetMethod?.Invoke(_target, args);

        Console.WriteLine($"* sync after: {res}");

        return res;
    }
}

class AsyncProxy<T> : DispatchProxy
{
    private T? _target;
    private AsyncAdvice? _beforeTask;
    private AsyncAdvice? _afterTask;

    public static T CreateProxy(T target, AsyncAdvice? beforeTask = null, AsyncAdvice? afterTask = null)
    {
        var proxy = Create<T, AsyncProxy<T>>();

        if (proxy is AsyncProxy<T> p)
        {
            p._target = target;
            p._beforeTask = beforeTask;
            p._afterTask = afterTask;
        }

        return proxy;
    }

    protected override object? Invoke(MethodInfo? targetMethod, object?[]? args)
    {
        var returnType = targetMethod?.ReturnType;

        if (returnType != null)
        {
            if (returnType == typeof(Task))
            {
                return InterceptAsync(targetMethod!, args);
            }
            else if (returnType.IsGenericType && returnType.GetGenericTypeDefinition() == typeof(Task<>))
            {
                var genericType = returnType.GetGenericArguments()[0];

                var method = typeof(AsyncProxy<T>).GetMethod("InterceptGenericAsync", BindingFlags.NonPublic | BindingFlags.Instance);
                var genericMethod = method!.MakeGenericMethod(genericType);

                return genericMethod.Invoke(this, new object?[]{targetMethod!, args});
            }
        }

        return targetMethod?.Invoke(_target, args);
    }

    private async Task InterceptAsync(MethodInfo targetMethod, object?[]? args)
    {
        if (_beforeTask != null)
        {
            await _beforeTask((targetMethod, args));
        }

        var r = targetMethod.Invoke(_target, args)!;

        if (r is Task task)
        {
            await task;
        }

        if (_afterTask != null)
        {
            await _afterTask((targetMethod, args));
        }
    }

    private async Task<R> InterceptGenericAsync<R>(MethodInfo targetMethod, object?[]? args)
    {
        if (_beforeTask != null)
        {
            await _beforeTask((targetMethod, args));
        }

        var res = await (Task<R>)targetMethod.Invoke(_target, args)!;

        if (_afterTask != null)
        {
            await _afterTask((targetMethod, args));
        }

        return res;
    }
}
