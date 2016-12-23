package sample;

import org.qi4j.api.mixin.Mixins;

@Mixins(SampleImpl.class)
public interface SampleComposite extends Sample {
}
