package sample;

import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.injection.scope.Service;

@Mixins(Sample.Mixin.class)
public interface Sample {
    void proc();

    class Mixin implements Sample {
        @Service
        private SampleService service;

        public void proc() {
            System.out.println(service.call());
        }
    }
}
