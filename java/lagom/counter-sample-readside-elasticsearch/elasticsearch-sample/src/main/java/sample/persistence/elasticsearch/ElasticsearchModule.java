package sample.persistence.elasticsearch;

import com.google.inject.AbstractModule;

public class ElasticsearchModule extends AbstractModule  {
    @Override
    protected void configure() {
        bind(ElasticsearchSampleSession.class).to(ElasticsearchSampleSessionImpl.class);
    }
}
