package sample;

import org.qi4j.api.concern.Concerns;
import org.qi4j.api.mixin.Mixins;

@Mixins(SampleImpl.class)
@Concerns(SampleConcern.class)
public interface SampleComposite extends Sample {
}
