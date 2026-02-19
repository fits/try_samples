using AspectInjector.Broker;

var s = new SimpleService();

s.FirstCheck();
s.SecondCheck(12);


[Aspect(Scope.Global)]
[Injection(typeof(BeforeLogger))]
public class BeforeLogger : Attribute
{
    [Advice(Kind.Before)]
    public void LogBefore(
        [Argument(Source.Type)] Type type, 
        [Argument(Source.Name)] string name, 
        [Argument(Source.Arguments)] object[] args
    )
    {
        Console.WriteLine($"*** before type={type}, name={name}, args={string.Join(",", args)}");
    }
}

[Aspect(Scope.Global)]
[Injection(typeof(AfterLogger))]
public class AfterLogger : Attribute
{
    [Advice(Kind.After)]
    public void LogAfter(
        [Argument(Source.Type)] Type type, 
        [Argument(Source.Name)] string name, 
        [Argument(Source.Arguments)] object[] args,
        [Argument(Source.ReturnType)] Type returnType,
        [Argument(Source.ReturnValue)] object returnValue
    )
    {
        Console.WriteLine($"*** after type={type}, name={name}, args={string.Join(",", args)}, returnValue={returnValue}, {returnType}");
    }
}

[BeforeLogger]
[AfterLogger]
class SimpleService
{
    public void FirstCheck()
    {
        Console.WriteLine($"FirstCheck");
    }

    public bool SecondCheck(int value)
    {
        Console.WriteLine($"SecondCheck: {value}");
        return true;
    }
}