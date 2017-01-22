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
		return errorHandle(req -> getEntity(id).ask(toCommand(req)));
	}

	@Override
	public ServiceCall<NotUsed, CounterState> findCounter(String id) {
		return errorHandle(req -> getEntity(id).ask(new CurrentState()));
	}

	private <P, R> HeaderServiceCall<P, R> errorHandle(ServiceCall<P, R> proc) {
		return (reqHeader, req) -> proc.invoke(req).handleAsync( (res, err) -> {
			ResponseHeader resHeader = ResponseHeader.OK;

			if (err != null) {
				resHeader = resHeader.withStatus(404);
			}

			return Pair.create(resHeader, res);
		});
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
