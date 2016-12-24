package sample;

import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.injection.scope.This;

import sample.config.AppConfig;

public class SampleServiceMixin implements SampleService {
    @This
    Configuration<AppConfig> config;

    @Override
    public String call() {
        return "name=" + config.get().name() + ", value=" + config.get().value();
    }
}
