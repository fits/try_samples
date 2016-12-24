package sample;

import org.qi4j.api.mixin.Mixins;

@Mixins(SampleServiceMixin.class)
public interface SampleService {
    String call();
}
