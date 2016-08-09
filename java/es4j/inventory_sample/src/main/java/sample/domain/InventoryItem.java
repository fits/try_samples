package sample.domain;

import com.eventsourcing.Model;
import com.eventsourcing.Repository;
import com.eventsourcing.queries.ModelQueries;

import lombok.Getter;

import sample.events.InventoryItemCreated;
import sample.protocols.InventoryItemNameProtocol;
import sample.protocols.InventoryItemCountProtocol;

import java.util.Optional;
import java.util.UUID;

public class InventoryItem implements Model, 
        InventoryItemNameProtocol, InventoryItemCountProtocol {

    @Getter
    private final Repository repository;
    @Getter
    private final UUID id;

    protected InventoryItem(Repository repository, UUID id) {
        this.repository = repository;
        this.id = id;
    }

    public static Optional<InventoryItem> lookup(Repository repository, UUID id) {

        Optional<InventoryItemCreated> res = ModelQueries.lookup(repository,
                InventoryItemCreated.class, InventoryItemCreated.ID, id);

        return res.map(ev -> new InventoryItem(repository, id));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InventoryItem &&
                getId().equals(((InventoryItem) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
