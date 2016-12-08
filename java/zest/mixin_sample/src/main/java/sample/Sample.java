package sample;

import org.qi4j.api.mixin.Mixins;

@Mixins(SampleMixin.class)
public interface Sample {
    String call();
}
