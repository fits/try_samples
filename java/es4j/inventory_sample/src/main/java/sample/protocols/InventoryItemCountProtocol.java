package sample.protocols;

import static com.eventsourcing.index.EntityQueryFactory.equal;

import com.eventsourcing.EntityHandle;
import com.eventsourcing.Protocol;
import lombok.val;

import sample.events.ItemsCheckedInToInventory;

import java.util.stream.StreamSupport;

public interface InventoryItemCountProtocol extends Protocol {
    default int count() {

        System.out.println("*** call count()");

        val res = getRepository().query(ItemsCheckedInToInventory.class,
                equal(ItemsCheckedInToInventory.REFERENCE_ID, getId()));

        System.out.println("*** call count(): " + res + ", " + res.size());

        return StreamSupport.stream(res.spliterator(), false)
                .map(EntityHandle::get)
                .mapToInt(ItemsCheckedInToInventory::getCount)
                .sum();
    }
}
