using System.Reflection;
using Castle.DynamicProxy;

var svc = new TestService();

var res = await svc.CheckStatusAsync("t1");

Console.WriteLine($"t1 result={res}");

var svc2 = new ProxyGenerator().CreateClassProxy<TestService>(new TestInterceptor());

var res2 = await svc2.CheckStatusAsync("t2_1");

Console.WriteLine($"t2 result={res2}");
Console.WriteLine($"t2 sync result={svc2.CheckStatus("t2_2")}");

var svc3 = new ProxyGenerator().CreateClassProxy<TestService>(new TestAsyncMethodInterceptor());

var res3 = await svc3.CheckStatusAsync("t3_1");

Console.WriteLine($"t3 result={res3}");
Console.WriteLine($"t3 sync result={svc3.CheckStatus("t3_2")}");


public class TestService
{
    private Random rand = new ();

    public virtual async Task<string> CheckStatusAsync(string target)
    {
        Console.WriteLine($"CheckStatusAsync sleep start: {target}");
        
        await Task.Delay(rand.Next(5, 200));

        Console.WriteLine($"CheckStatusAsync sleep end: {target}");

        return $"ok-{target}";
    }

    public virtual string CheckStatus(string target)
    {
        return $"ok-{target}";
    }
}

class TestInterceptor : IInterceptor
{
    public void Intercept(IInvocation invocation)
    {
        Console.WriteLine($"* intercept before: {invocation.GetArgumentValue(0)}");

        invocation.Proceed();

        Console.WriteLine($"* intercept after: {invocation.GetArgumentValue(0)}");
    }
}

class TestAsyncMethodInterceptor : IInterceptor
{
    public void Intercept(IInvocation invocation)
    {
        var returnType = invocation.Method.ReturnType;

        if (returnType.IsGenericType && returnType.GetGenericTypeDefinition() == typeof(Task<>))
        {
            Type genericType = returnType.GetGenericArguments()[0];

            var method = GetType().GetMethod("InterceptAsync", BindingFlags.NonPublic | BindingFlags.Instance);
            var genericMethod = method!.MakeGenericMethod(genericType);

            invocation.ReturnValue = genericMethod.Invoke(this, new []{invocation});
        }
        else
        {
            invocation.Proceed();
        }
    }

    private async Task<T> InterceptAsync<T>(IInvocation invocation)
    {
        var proceed = invocation.CaptureProceedInfo();

        await BeforeAsync(invocation);
        
        proceed.Invoke();
        var res = await (Task<T>)invocation.ReturnValue!;
        
        await AfterAsync(invocation);

        return res;
    }

    private async Task BeforeAsync(IInvocation invocation)
    {
        await Task.Delay(10);
        Console.WriteLine($"** async-intercept before: {invocation.GetArgumentValue(0)}");
    }

    private async Task AfterAsync(IInvocation invocation)
    {
        await Task.Delay(50);
        Console.WriteLine($"** async-intercept after: {invocation.GetArgumentValue(0)}");
    }
}