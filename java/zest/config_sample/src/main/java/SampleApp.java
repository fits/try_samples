
import org.qi4j.api.activation.ActivationException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;
import org.qi4j.entitystore.memory.MemoryEntityStoreAssembler;
import org.qi4j.valueserialization.jackson.JacksonValueSerializationAssembler;

import sample.Sample;
import sample.SampleService;
import sample.config.AppConfig;

public class SampleApp {
    public static void main(String... args) throws ActivationException, AssemblyException {

        SingletonAssembler assem = new SingletonAssembler() {
            @Override
            public void assemble(ModuleAssembly module) throws AssemblyException {
                module.transients(Sample.class);
                module.addServices(SampleService.class);
                module.configurations(AppConfig.class);

                new MemoryEntityStoreAssembler().assemble(module);
                new JacksonValueSerializationAssembler().assemble(module);
            }
        };

        Sample sample = assem.module().newTransient(Sample.class);

        sample.proc();
    }
}
