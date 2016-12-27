package sample.domain;

import com.eventsourcing.Model;
import com.eventsourcing.Repository;
import com.eventsourcing.queries.ModelQueries;
import lombok.EqualsAndHashCode;
import lombok.Value;
import sample.events.InventoryItemCreated;
import sample.protocols.InventoryItemCountProtocol;
import sample.protocols.InventoryItemNameProtocol;

import java.util.Optional;
import java.util.UUID;

@Value
@EqualsAndHashCode(of = "id")
public class InventoryItem implements Model, InventoryItemNameProtocol, InventoryItemCountProtocol {
    private final Repository repository;
    private final UUID id;

    protected InventoryItem(Repository repository, UUID id) {
        this.repository = repository;
        this.id = id;
    }

    public static Optional<InventoryItem> lookup(Repository repository, UUID id) {

        Optional<InventoryItemCreated> res = ModelQueries.lookup(repository,
                InventoryItemCreated.class, InventoryItemCreated.ID, id);

        System.out.println(res);

        return res.map(ev -> new InventoryItem(repository, id));
    }
}
