package sample.entity;

import org.qi4j.api.property.Property;
import org.qi4j.library.eventsourcing.domain.api.DomainEvent;

public interface Data {
    Property<String> name();
    Property<Integer> value();

    @DomainEvent
    void addValue(int value);
}
