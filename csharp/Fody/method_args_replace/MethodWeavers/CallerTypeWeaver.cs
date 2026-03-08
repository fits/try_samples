using Fody;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace MethodWeavers;

public class CallerTypeWeaver : BaseModuleWeaver
{
    public override void Execute()
    {
        var ts = ModuleDefinition.GetTypes().Where(x => x.BaseType != null && !x.IsEnum && !x.IsInterface);

        foreach (var t in ts)
        {
            foreach (var m in t.Methods)
            {
                var ilProc = m.Body.GetILProcessor();

                foreach (var inst in m.Body.Instructions)
                {
                    if (inst.OpCode == OpCodes.Call && inst.Operand is MethodReference mr )
                    {
                        if (mr.DeclaringType.FullName == "TestProject.LogUtil" && mr.Name == "Info")
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
    }

    public override IEnumerable<string> GetAssembliesForScanning() => [];
}
