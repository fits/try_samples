package sample.protocols;

import static com.eventsourcing.index.EntityQueryFactory.*;

import com.eventsourcing.EntityHandle;
import com.eventsourcing.Protocol;
import com.googlecode.cqengine.query.Query;

import lombok.val;

import java.util.stream.StreamSupport;

import sample.events.ItemsCheckedInToInventory;

public interface InventoryItemCountProtocol extends Protocol {

    default int count() {
        val queryOpt = equal(ItemsCheckedInToInventory.REFERENCE_ID, getId());

        try (val resultSet = getRepository().query(ItemsCheckedInToInventory.class, queryOpt)) {
            return StreamSupport.stream(resultSet.spliterator(), false)
                .map(EntityHandle::get)
                .mapToInt(ItemsCheckedInToInventory::getCount)
                .sum();
        }
    }
}
