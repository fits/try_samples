@Grab('org.axonframework:axon-core:3.0-M3')
@Grab('org.slf4j:slf4j-simple:1.7.21')
import org.axonframework.commandhandling.AggregateAnnotationCommandHandler
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.TargetAggregateIdentifier

import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle

import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import groovy.transform.Immutable

@Immutable
class CreateDataCommand {
	String id
	int value
}

@Immutable
class UpdateDataCommand {
	@TargetAggregateIdentifier
	String id
	int valueDiff
}

@Immutable
class DataCreatedEvent {
	String id
	int value
}

@Immutable
class DataUpdatedEvent {
	int valueDiff
}

class Data {
	@AggregateIdentifier
	private String id
	private int value

	public Data() {
		println '*** default constructor'
	}

	@CommandHandler
	public Data(CreateDataCommand cmd) {
		println '*** new Data'
		AggregateLifecycle.apply(new DataCreatedEvent(cmd.id, cmd.value))
	}

	@CommandHandler
	private void updateValue(UpdateDataCommand cmd) {
		println '*** update value'
		AggregateLifecycle.apply(new DataUpdatedEvent(cmd.valueDiff))
	}

	@EventSourcingHandler
	private void applyCreated(DataCreatedEvent event) {
		println '*** applyCreated'

		this.id = event.id
		this.value = event.value
	}

	@EventSourcingHandler
	private void applyUpdated(DataUpdatedEvent event) {
		println "*** applyUpdated: id=${this.id}, oldValue=${this.value}"

		this.value += event.valueDiff
	}
}

def es = new EmbeddedEventStore(new InMemoryEventStorageEngine())
def repo = new EventSourcingRepository(Data, es)

def cmdHandler = new AggregateAnnotationCommandHandler(Data, repo)

def cmdBus = new SimpleCommandBus()

cmdHandler.subscribe(cmdBus)

def gateway = new DefaultCommandGateway(cmdBus)

def callback = [
	onSuccess: {m, r -> println "success: ${r}" },
	onFailure: {m, e -> println "failure: ${e}" }
] as CommandCallback

gateway.send(new CreateDataCommand('d1', 10), callback)

println '----------'

gateway.send(new UpdateDataCommand('d1', 5), callback)

println '----------'

gateway.send(new UpdateDataCommand('d1', 3), callback)
