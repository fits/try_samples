package sample.protocols;

import com.eventsourcing.EntityHandle;
import com.eventsourcing.Protocol;

import lombok.val;

import java.util.stream.StreamSupport;

import sample.events.ItemsCheckedInToInventory;

import static com.eventsourcing.index.EntityQueryFactory.equal;

public interface InventoryItemCountProtocol extends Protocol {

    default int count() {
        val res = getRepository().query(ItemsCheckedInToInventory.class,
                equal(ItemsCheckedInToInventory.REFERENCE_ID, getId()));

        return StreamSupport.stream(res.spliterator(), false)
                .map(EntityHandle::get)
                .mapToInt(ItemsCheckedInToInventory::getCount)
                .sum();
    }
}
