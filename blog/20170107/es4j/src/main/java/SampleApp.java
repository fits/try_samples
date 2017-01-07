
import com.eventsourcing.*;
import com.eventsourcing.hlc.PhysicalTimeProvider;
import com.eventsourcing.index.MemoryIndexEngine;
import com.eventsourcing.inmem.MemoryJournal;
import com.eventsourcing.repository.StandardRepository;

import com.google.common.util.concurrent.AbstractService;

import java.util.concurrent.*;

import lombok.val;

import sample.commands.CheckInItemsToInventory;
import sample.commands.CreateInventoryItem;
import sample.domain.InventoryItem;
import sample.events.InventoryItemCreated;

public class SampleApp {
    public static void main(String... args) {

        val repository = StandardRepository.builder()
                .journal(new MemoryJournal())
                .indexEngine(new MemoryIndexEngine())
                .physicalTimeProvider(new SampleTimeProvider())
                .build();

        repository.addCommandSetProvider(
            new PackageCommandSetProvider(
                new Package[] {CreateInventoryItem.class.getPackage()}
            )
        );

        repository.addEventSetProvider(
            new PackageEventSetProvider(
                new Package[]{InventoryItemCreated.class.getPackage()}
            )
        );

        repository.startAsync().awaitRunning();

        repository.publish(new CreateInventoryItem("sample1"))
            .thenApply(SampleApp::dumpInventoryItem)
            .thenCompose(d ->
                repository.publish(new CheckInItemsToInventory(d.getId(), 5))
                    .thenApply(v -> d)
            )
            .thenApply(SampleApp::dumpInventoryItem)
            .thenCompose(d ->
                repository.publish(new CheckInItemsToInventory(d.getId(), 3))
                    .thenApply(v -> d)
            )
            .thenApply(SampleApp::dumpInventoryItem)
            .whenComplete((d, e) -> stopRepository(repository));
    }

    private static InventoryItem dumpInventoryItem(InventoryItem item) {
        System.out.println("----- InventoryItem -----");

        System.out.println("id: " + item.getId());
        System.out.println("name: " + item.name());
        System.out.println("count: " + item.count());

        return item;
    }

    private static void stopRepository(Repository repository) {
        System.out.println("stop...");

        try {
            repository.stopAsync().awaitTerminated(10, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            ex.printStackTrace();
        }
    }

    static class SampleTimeProvider extends AbstractService
            implements PhysicalTimeProvider {

        @Override
        public long getPhysicalTime() {
            return System.currentTimeMillis();
        }

        @Override
        protected void doStart() {
            System.out.println("timeprovider start...");
            notifyStarted();
        }

        @Override
        protected void doStop() {
            System.out.println("timeprovider stop...");
            notifyStopped();
        }
    }
}
