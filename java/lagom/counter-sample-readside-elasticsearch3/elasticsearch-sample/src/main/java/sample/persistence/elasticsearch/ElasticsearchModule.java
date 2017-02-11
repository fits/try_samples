package sample.persistence.elasticsearch;

import com.google.inject.AbstractModule;
import sample.persistence.elasticsearch.impl.ElasticsearchOffsetStoreImpl;
import sample.persistence.elasticsearch.impl.ElasticsearchReadSideImpl;
import sample.persistence.elasticsearch.impl.ElasticsearchSessionImpl;

public class ElasticsearchModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ElasticsearchOffsetStore.class).to(ElasticsearchOffsetStoreImpl.class);
        bind(ElasticsearchSession.class).to(ElasticsearchSessionImpl.class);
        bind(ElasticsearchReadSide.class).to(ElasticsearchReadSideImpl.class);
    }
}
