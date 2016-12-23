
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;

import org.qi4j.entitystore.memory.MemoryEntityStoreAssembler;
import org.qi4j.io.Outputs;
import org.qi4j.io.Transforms;
import org.qi4j.library.eventsourcing.bootstrap.EventsourcingAssembler;
import org.qi4j.library.eventsourcing.domain.api.DomainEventValue;
import org.qi4j.library.eventsourcing.domain.api.UnitOfWorkDomainEventsValue;
import org.qi4j.library.eventsourcing.domain.factory.DomainEventCreationConcern;
import org.qi4j.library.eventsourcing.domain.factory.DomainEventFactoryService;
import org.qi4j.library.eventsourcing.domain.source.EventSource;
import org.qi4j.library.eventsourcing.domain.source.EventStoreActivation;
import org.qi4j.library.eventsourcing.domain.source.memory.MemoryEventStoreService;
import org.qi4j.valueserialization.orgjson.OrgJsonValueSerializationAssembler;
import sample.SampleComposite;
import sample.entity.DataEntity;
import sample.entity.DataEntityFactoryService;

public class SampleApp {
    public static void main(String... args) throws Exception {

        SingletonAssembler assembler = new SingletonAssembler() {
            @Override
            public void assemble(ModuleAssembly module) throws AssemblyException {
                module.transients(SampleComposite.class);

                module.services(DataEntityFactoryService.class);

                module.entities(DataEntity.class)
                        .withConcerns(DomainEventCreationConcern.class);

                new OrgJsonValueSerializationAssembler().assemble(module);
                new MemoryEntityStoreAssembler().assemble(module);

                // Module Settings for EventSourcing
                new EventsourcingAssembler()
                        .withCurrentUserFromUOWPrincipal()
                        .assemble(module);

                module.services(MemoryEventStoreService.class, DomainEventFactoryService.class);
                module.values(DomainEventValue.class, UnitOfWorkDomainEventsValue.class);
            }
        };

        try (UnitOfWork uow = assembler.module().newUnitOfWork()) {
            SampleComposite sample = assembler.module().newTransient(SampleComposite.class);

            System.out.println(sample.call());

            uow.complete();
        }

        EventSource es = assembler.module().findService(EventSource.class).get();

        System.out.println(es);

        System.out.println("events count: " + es.count());

        es.events(0, Long.MAX_VALUE)
                .transferTo(
                        Transforms.map(UnitOfWorkDomainEventsValue::toString, Outputs.systemOut()));

        // stop process
        assembler.module().findService(EventStoreActivation.class).get()
                .passivateEventStore();
        //((EventStoreActivation)es).passivateEventStore();
    }
}
