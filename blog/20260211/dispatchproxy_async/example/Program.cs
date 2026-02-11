using System.Reflection;

var svc = DispatchProxy.Create<ICheckService, SimpleProxy>();

var r = svc.FirstCheck("check1");
Console.WriteLine(r);

svc.SecondCheck("check2", 123);

interface ICheckService
{
    string FirstCheck(string a);
    void SecondCheck(string a, int b);
}

class SimpleProxy : DispatchProxy
{
    protected override object? Invoke(MethodInfo? targetMethod, object?[]? args)
    {
        Console.WriteLine($"* Invoke: method={targetMethod}, args={string.Join(',', args ?? [])}");
        return "ok";
    }
}