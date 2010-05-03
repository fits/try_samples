package fits.sample;

import org.qi4j.api.composite.CompositeBuilderFactory;
import org.qi4j.bootstrap.SingletonAssembler;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.AssemblyException;

public class App {
    public static void main( String[] args ) {
    	SingletonAssembler ass = new SingletonAssembler() {
    		public void assemble(ModuleAssembly module) throws AssemblyException {
    			module.addComposites(SampleComposite.class);
    		}
    	};

    	CompositeBuilderFactory factory = ass.compositeBuilderFactory();
    	Sample sample = factory.newComposite(Sample.class);

		System.out.println(sample.hello("test"));
    }
}
