package sample;

import org.elasticsearch.common.inject.AbstractModule;

public class SampleModule extends AbstractModule {
    @Override
    protected void configure() {
        System.out.println("*** call SampleModule.configure()");

        bind(SampleIndexWatcher.class).asEagerSingleton();
    }
}
