package sample.counter.impl;

import akka.NotUsed;
import akka.japi.Pair;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;
import java.util.UUID;

import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import sample.counter.api.Counter;
import sample.counter.api.CounterParameter;
import sample.counter.api.CounterService;
import sample.counter.api.CreateParameter;

import sample.counter.impl.CounterCommand.*;

public class CounterServiceImpl implements CounterService {
	private final PersistentEntityRegistry persistentEntityRegistry;

	@Inject
	public CounterServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
		this.persistentEntityRegistry = persistentEntityRegistry;
		persistentEntityRegistry.register(CounterEntity.class);
	}

	@Override
	public ServiceCall<CreateParameter, Counter> createCounter() {
		String id = UUID.randomUUID().toString();
		return req -> getEntity(id).ask(toCommand(req));
	}

	@Override
	public ServiceCall<CounterParameter, Counter> countUp(String id) {
		return req -> getEntity(id).ask(toCommand(req));
	}

	@Override
	public ServiceCall<NotUsed, Counter> findCounter(String id) {
		return req -> getEntity(id).ask(new CurrentState());
	}

	private CounterCreate toCommand(CreateParameter param) {
		return new CounterCreate(param.getName());
	}

	private CounterAdd toCommand(CounterParameter param) {
		return new CounterAdd(param.getCount());
	}

	private PersistentEntityRef<CounterCommand> getEntity(String id) {
		return persistentEntityRegistry.refFor(CounterEntity.class, id);
	}
}
