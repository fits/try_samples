using Fody;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace CallerWeavers;

public class CallerTypeWeaver : BaseModuleWeaver
{
    private const string TARGET_ATTRIBUTE = "CustomAttributes.CallerTypeAttribute";

    public override void Execute()
    {
        var filter = (TypeDefinition x) => x.BaseType != null && !x.IsEnum && !x.IsInterface;

        var ts = ModuleDefinition.GetTypes().Where(filter);

        foreach (var t in ts)
        {
            ProcessType(t);

            foreach (var nt in t.NestedTypes.Where(filter))
            {
                ProcessType(nt);
            }
        }
    }

    private void ProcessType(TypeDefinition t)
    {
        foreach (var m in t.Methods)
        {
            foreach (var inst in m.Body.Instructions)
            {
                if (inst.OpCode == OpCodes.Call && inst.Operand is MethodReference mr )
                {
                    var lastParam = mr.Parameters.LastOrDefault();

                    if (lastParam != null && lastParam.CustomAttributes.Any(x => x.AttributeType.FullName == TARGET_ATTRIBUTE))
                    {
                        var prevInst = inst.Previous;

                        if (prevInst.OpCode == OpCodes.Ldstr)
                        {
                            prevInst.Operand = t.FullName;
                        }
                    }
                }
            }
        }
    }

    public override IEnumerable<string> GetAssembliesForScanning() => [];
}
