import org.qi4j.api.activation.ActivationException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;
import sample.Sample;

public class App {
    public static void main(String... args) throws ActivationException, AssemblyException {

        SingletonAssembler assem = new SingletonAssembler() {
            @Override
            public void assemble(ModuleAssembly module) throws AssemblyException {
                module.transients(Sample.class);
            }
        };

        Sample sample = assem.module().newTransient(Sample.class);

        System.out.println(sample.call());
    }
}
