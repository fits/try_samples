using System.Reflection;

var proxy = GreetingProxy.CreateProxy(new Greeting());

Console.WriteLine($"result = {proxy.Hello("abc")}");


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