package sample;

import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class SampleActor extends AbstractPersistentActor {
    private final String id;
    private int state = 0;

    public SampleActor(String id) {
        this.id = id;
    }

    @Override
    public String persistenceId() {
        return id;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveRecover() {
        return ReceiveBuilder
                .match(AddedCounter.class, this::updateState)
                .match(SnapshotOffer.class, ss -> state = (Integer)ss.snapshot())
                .build();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveCommand() {
        return ReceiveBuilder
                .match(CounterAdd.class, cmd ->
                        persist(new AddedCounter(cmd.count()), event -> {
                            updateState(event);
                            context().system().eventStream().publish(event);
                        })
                )
                .matchEquals("snapshot", cmd -> saveSnapshot(state))
                .matchEquals("dump", cmd -> System.out.println("counter: " + state))
                .matchEquals("end", cmd -> context().system().terminate())
                .build();
    }

    private void updateState(AddedCounter event) {
        state += event.count();
    }
}
