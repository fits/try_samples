
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
import sample.events.InventoryItemCreated;

public class SampleApp {
    public static void main(String... args) {

        val repository = StandardRepository.builder()
                .journal(new MemoryJournal())
                .indexEngine(new MemoryIndexEngine())
                .physicalTimeProvider(new SampleTimeProvider())
                .build();

        repository.addCommandSetProvider(
            new PackageCommandSetProvider(new Package[] {CreateInventoryItem.class.getPackage()}));

        repository.addEventSetProvider(
            new PackageEventSetProvider(new Package[]{InventoryItemCreated.class.getPackage()}));

        repository.startAsync().awaitRunning();

        try {
            val d = repository.publish(new CreateInventoryItem("sample1")).get();

            System.out.println("id: " + d.getId());
            System.out.println("name: " + d.name());
            System.out.println("count: " + d.count());

            repository.publish(new CheckInItemsToInventory(d.getId(), 5)).get();

            System.out.println("count: " + d.count());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            System.out.println("stop...");

            try {
                repository.stopAsync().awaitTerminated(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    static class SampleTimeProvider extends AbstractService implements PhysicalTimeProvider {

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
