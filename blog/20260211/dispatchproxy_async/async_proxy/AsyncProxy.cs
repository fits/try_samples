using System.Reflection;
using AsyncFunc = System.Func<(System.Reflection.MethodInfo targetMethod, object?[]? args), System.Threading.Tasks.Task>;

public class AsyncProxy<T> : DispatchProxy
{
    private T? _target;
    private AsyncFunc? _beforeTask;
    private AsyncFunc? _afterTask;

    public static T CreateProxy(T target, AsyncFunc? beforeTask = null, AsyncFunc? afterTask = null)
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
