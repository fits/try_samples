package sample.config;

import org.qi4j.api.property.Property;

public interface AppConfig {
    Property<String> name();
    Property<Integer> value();
}
