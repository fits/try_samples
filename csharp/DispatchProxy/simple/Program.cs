using System.Reflection;

var proxy = GreetingProxy.CreateProxy(new Greeting());
Console.WriteLine($"result = {proxy.Hello("abc")}");

var proxy2 = DispatchProxy.Create<ITestService, TestProxy>();

Console.WriteLine($"check1 = {proxy2.Check1(123)}");

proxy2.Check2("ab", 1);

Console.WriteLine($"check2 ok");

try 
{
    proxy2.Check3(1, true, "a");
}
catch(InvalidCastException e)
{
    Console.WriteLine($"check3 cast error: {e}");
}


interface IGreeting
{
    string Hello(string name);
}

class Greeting : IGreeting
{
    public string Hello(string name)
    {
        return $"Hi {name}";
    }
}

class GreetingProxy : DispatchProxy
{
    private IGreeting? _target;

    public static IGreeting CreateProxy(IGreeting target)
    {
        var proxy = Create<IGreeting, GreetingProxy>();

        if (proxy is GreetingProxy p)
        {
            p._target = target;
        }

        return proxy;
    }

    protected override object? Invoke(MethodInfo? targetMethod, object?[]? args)
    {
        Console.WriteLine($"* before invoke");

        var res = targetMethod?.Invoke(_target, args);

        Console.WriteLine($"* after invoke: {res}");

        return res;
    }
}

interface ITestService
{
    string Check1(int a);
    void Check2(string a, int b);
    bool Check3(int a, bool b, string c);
}

class TestProxy : DispatchProxy
{
    protected override object? Invoke(MethodInfo? targetMethod, object?[]? args)
    {
        Console.WriteLine($"* called invoke: method={targetMethod}, args={args}");

        return "test";
    }
}