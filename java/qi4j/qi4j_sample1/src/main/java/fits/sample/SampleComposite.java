package fits.sample;

import org.qi4j.api.composite.Composite;
import org.qi4j.api.mixin.Mixins;

@Mixins({SampleMixin.class})
public interface SampleComposite extends Sample, Composite {
}
