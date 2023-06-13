using Microsoft.AspNetCore.Mvc;

public class SampleController : Controller
{
    public string Index()
    {
        return "sample";
    }

    public string Check()
    {
        return "check";
    }
}