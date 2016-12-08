import org.qi4j.api.activation.ActivationException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;

import sample.SampleComposite;

public class App {
    public static void main(String... args) throws ActivationException, AssemblyException {

        SingletonAssembler assembler = new SingletonAssembler() {
            @Override
            public void assemble(ModuleAssembly module) throws AssemblyException {
                module.transients(SampleComposite.class);
            }
        };

        SampleComposite sample = assembler.module().newTransient(SampleComposite.class);

        System.out.println(sample.call());
    }
}
