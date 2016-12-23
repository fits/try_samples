package sample.entity;

import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.mixin.Mixins;

@Mixins(DataEntity.Mixin.class)
public interface DataEntity extends Data, EntityComposite {

    abstract class Mixin implements Data {
        @Override
        public void addValue(int add) {
            value().set(value().get() + add);
        }
    }
}
