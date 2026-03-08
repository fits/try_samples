using Fody;
using Mono.Cecil;

namespace SimpleWeavers;

public class InterfaceWeaver : BaseModuleWeaver
{
    public override void Execute()
    {
        WriteInfo($"weaver execute start: assembly={AssemblyFilePath}");

        var type = new TypeDefinition("WeavingTest", "ITest", TypeAttributes.Public | TypeAttributes.Interface | TypeAttributes.Abstract);

        ModuleDefinition.Types.Add(type);

        WriteInfo("weaver execute end");
    }

    public override IEnumerable<string> GetAssembliesForScanning() => [];
}
