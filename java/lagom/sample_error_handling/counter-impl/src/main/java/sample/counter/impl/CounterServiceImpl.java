package sample.counter.impl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;
import java.util.UUID;

import sample.counter.api.CounterParameter;
import sample.counter.api.CounterService;
import sample.counter.api.CounterState;
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
	public ServiceCall<CreateParameter, CounterState> createCounter() {
		String id = UUID.randomUUID().toString();
		return req -> getEntity(id).ask(toCommand(req));
	}

	@Override
	public ServiceCall<CounterParameter, CounterState> countUp(String id) {
		return req -> getEntity(id).ask(toCommand(req));
	}

	@Override
	public ServiceCall<NotUsed, CounterState> findCounter(String id) {
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
