package sample.counter.impl;

import akka.NotUsed;

import akka.japi.Pair;
import akka.japi.pf.Match;
import akka.japi.pf.PFBuilder;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import akka.util.ByteString.ByteStrings;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import sample.counter.api.CountMessage;
import sample.counter.api.Counter;
import sample.counter.api.CounterService;
import sample.counter.impl.CounterCommand.*;

import java.nio.file.Paths;
import java.util.UUID;

public class CounterServiceImpl implements CounterService {
    private static final String OFFSET_FILE = "offset.dat";

    private final PersistentEntityRegistry entityRegistry;
    private final Materializer materializer;

    @Inject
    public CounterServiceImpl(PersistentEntityRegistry entityRegistry,
                              Materializer materializer) {

        this.entityRegistry = entityRegistry;
        entityRegistry.register(CounterEntity.class);

        this.materializer = materializer;

        offsetSource()
                .flatMapConcat(offset -> entityRegistry.eventStream(CounterEvent.TAG, offset))
                .runForeach(this::subscribe, materializer);
    }

    @Override
    public ServiceCall<NotUsed, Counter> create(String id) {
        return req -> getEntity(id).ask(new CreateCounter());
    }

    @Override
    public ServiceCall<NotUsed, Counter> find(String id) {
        return req -> getEntity(id).ask(new CurrentCounter());
    }

    @Override
    public ServiceCall<CountMessage, Counter> update(String id) {
        return req -> getEntity(id).ask(new UpdateCounter(req.getCount()));
    }

    private void subscribe(Pair<CounterEvent, Offset> data) {
        System.out.println("*** subscribe: " + data);
        saveOffset(data.second());
    }

    private PersistentEntityRef<CounterCommand> getEntity(String id) {
        return entityRegistry.refFor(CounterEntity.class, id);
    }

    private void saveOffset(Offset offset) {
        Source.single(offset)
                .map(Offset::toString)
                .map(ByteStrings::fromString)
                .runWith(FileIO.toPath(Paths.get(OFFSET_FILE)), materializer);
    }

    private Source<Offset, ?> offsetSource() {
        PFBuilder<Throwable, Offset> pfunc = Match.match(Exception.class, e -> Offset.NONE);

        return FileIO.fromPath(Paths.get(OFFSET_FILE))
                .map(ByteString::utf8String)
                .map(this::toOffset)
                .recover(pfunc.build());
    }

    private Offset toOffset(String str) {
        if (str.contains("-")) {
            return Offset.timeBasedUUID(UUID.fromString(str));
        }
        else {
            return Offset.sequence(Long.parseLong(str));
        }
    }
}
