package sample.entity;

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.structure.Module;

@Mixins(DataEntityFactoryService.Mixin.class)
public interface DataEntityFactoryService extends DataEntityFactory {

    class Mixin implements DataEntityFactory {
        @Structure
        private Module module;

        @Override
        public Data create(String name, int value) {
            System.out.println("module: " + module);

            Data data = module.currentUnitOfWork().newEntityBuilder(Data.class).instance();

            data.name().set(name);
            data.value().set(value);

            return data;
        }
    }
}
