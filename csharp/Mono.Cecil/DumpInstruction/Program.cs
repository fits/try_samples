using Mono.Cecil;

var assm = AssemblyDefinition.ReadAssembly(args[0]);

foreach (var t in assm.MainModule.Types)
{
    Console.WriteLine($"----- type={t.FullName} -----");

    foreach (var m in t.Methods)
    {
        Console.WriteLine($"method={m.Name}, {m.FullName}");

        foreach (var inst in m.Body.Instructions)
        {
            Console.WriteLine($"  instruction={inst}");
        }
    }
}