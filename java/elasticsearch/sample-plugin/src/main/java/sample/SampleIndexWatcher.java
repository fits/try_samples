package sample;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.indices.IndicesLifecycle;
import org.elasticsearch.indices.IndicesService;

public class SampleIndexWatcher {

    @Inject
    public SampleIndexWatcher(IndicesService service) {

        System.out.println("*** SampleIndexWatcher : " + settings);

        service.indicesLifecycle().addListener(new IndicesLifecycle.Listener() {
            @Override
            public void afterIndexCreated(IndexService indexService) {
                System.out.println("*** index created: " + indexService.index().getName());
            }

            @Override
            public void afterIndexDeleted(Index index, Settings indexSettings) {
                System.out.println("*** index deleted: " + index.getName());
            }
        });

    }
}
