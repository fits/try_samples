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

await svc3.CheckStatus2Async("t3_2");

Console.WriteLine($"t3 sync result={svc3.CheckStatus("t3_3")}");

var svc4 = new ProxyGenerator().CreateClassProxy<TestService>(new TestInterceptor1(), new TestInterceptor2(), new TestInterceptor3());

var res4 = await svc4.CheckStatusAsync("t4_1");

Console.WriteLine($"t4 result={res4}");

await svc4.CheckStatus2Async("t4_2");

Console.WriteLine($"t4 sync result={svc4.CheckStatus("t4_3")}");


public class TestService
{
    private Random rand = new ();

    public virtual async Task<string> CheckStatusAsync(string target)
    {
        Console.WriteLine($"CheckStatusAsync sleep start: {target}, thread={Thread.CurrentThread.ManagedThreadId}");
        
        await Task.Delay(rand.Next(5, 200));

        Console.WriteLine($"CheckStatusAsync sleep end: {target}, thread={Thread.CurrentThread.ManagedThreadId}");

        return $"ok-{target}";
    }

    public virtual async Task CheckStatus2Async(string target)
    {
        Console.WriteLine($"CheckStatus2Async sleep start: {target}, thread={Thread.CurrentThread.ManagedThreadId}");
        
        await Task.Delay(rand.Next(5, 200));

        Console.WriteLine($"CheckStatus2Async sleep end: {target}, thread={Thread.CurrentThread.ManagedThreadId}");
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
        Console.WriteLine($"* intercept before: {invocation.GetArgumentValue(0)}, thread={Thread.CurrentThread.ManagedThreadId}");

        invocation.Proceed();

        Console.WriteLine($"* intercept after: {invocation.GetArgumentValue(0)}, thread={Thread.CurrentThread.ManagedThreadId}");
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
        Console.WriteLine($"** async-intercept before: {invocation.GetArgumentValue(0)}, thread={Thread.CurrentThread.ManagedThreadId}");
    }

    private async Task AfterAsync(IInvocation invocation)
    {
        await Task.Delay(50);
        Console.WriteLine($"** async-intercept after: {invocation.GetArgumentValue(0)}, thread={Thread.CurrentThread.ManagedThreadId}");
    }
}

class TestInterceptor1 : IInterceptor
{
    public void Intercept(IInvocation invocation)
    {
        InterceptorUtil.AsyncAdvice(invocation);
    }
}

class TestInterceptor2 : IInterceptor
{
    public void Intercept(IInvocation invocation)
    {
        InterceptorUtil.AsyncAdvice(invocation, async (x) => 
        {
            await Task.Delay(10);
            Console.WriteLine($"*** intercept2 before: {x.GetArgumentValue(0)}, thread={Thread.CurrentThread.ManagedThreadId}");
        });
    }
}

class TestInterceptor3 : IInterceptor
{
    public void Intercept(IInvocation invocation)
    {
        InterceptorUtil.AsyncAdvice(invocation, afterTask: async (x) => 
        {
            await Task.Delay(10);
            Console.WriteLine($"*** intercept3 after: {x.GetArgumentValue(0)}, thread={Thread.CurrentThread.ManagedThreadId}");
        });
    }
}
