package sample.inventory.impl;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import sample.inventory.api.InventoryCreateParameter;
import sample.inventory.api.InventoryItem;
import sample.inventory.api.InventoryService;

import sample.inventory.impl.InventoryCommand.*;

public class InventoryServiceImpl implements InventoryService {
    private final PersistentEntityRegistry persistentEntityRegistry;

    @Inject
    public InventoryServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {

        this.persistentEntityRegistry = persistentEntityRegistry;

        persistentEntityRegistry.register(InventoryEntity.class);
    }

    @Override
    public ServiceCall<InventoryCreateParameter, Done> create(String id) {
        return req -> getEntityRef(id).ask(new CreateInventoryItem(id, req.getName()));
    }

    @Override
    public ServiceCall<NotUsed, Done> addStock(String id, int count) {
        return req -> getEntityRef(id).ask(new CheckInItemsToInventory(count));
    }

    @Override
    public ServiceCall<NotUsed, InventoryItem> state(String id) {
        return req -> getEntityRef(id).ask(new CurrentState());
    }

    private PersistentEntityRef<InventoryCommand> getEntityRef(String id) {
        return persistentEntityRegistry.refFor(InventoryEntity.class, id);
    }
}
