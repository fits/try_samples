
import com.eventsourcing.CommandSetProvider;
import com.eventsourcing.EventSetProvider;
import com.eventsourcing.PackageCommandSetProvider;
import com.eventsourcing.PackageEventSetProvider;
import com.eventsourcing.hlc.HybridTimestamp;
import com.eventsourcing.hlc.NTPServerTimeProvider;
import com.eventsourcing.hlc.PhysicalTimeProvider;
import com.eventsourcing.index.IndexEngine;
import com.eventsourcing.index.MemoryIndexEngine;
import com.eventsourcing.inmem.MemoryJournal;
import com.eventsourcing.repository.CommandConsumer;
import com.eventsourcing.repository.StandardRepository;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;

import lombok.val;

import sample.commands.CreateInventoryItem;
import sample.events.InventoryItemCreated;

import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class SampleApp {
    public static void main(String... args) throws TimeoutException, UnknownHostException {

        val repository = new StandardRepository();

        repository.setJournal(new MemoryJournal());
        repository.setIndexEngine(new MemoryIndexEngine());
        repository.setPhysicalTimeProvider(new SampleTimeProvider());

        try {
            System.out.println("start...");
            System.out.println(repository.state());

            repository.startAsync().awaitRunning(10, TimeUnit.SECONDS);

            System.out.println("started");
            System.out.println(repository.state());

            repository.addCommandSetProvider(createCommandSetProvider());
            repository.addEventSetProvider(createEventSetProvider());

            val cmd = new CreateInventoryItem("sample1");

            val res = repository.publish(cmd).get();

            System.out.println("id: " + res.getId());
            System.out.println("name: " + res.name());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            System.out.println("stop...");
            repository.stopAsync().awaitTerminated(10, TimeUnit.SECONDS);
        }
    }

    private static CommandSetProvider createCommandSetProvider() {
        val pkg = new Package[] { CreateInventoryItem.class.getPackage() };

        return new PackageCommandSetProvider(pkg);
    }

    private static EventSetProvider createEventSetProvider() {
        val pkg = new Package[]{ InventoryItemCreated.class.getPackage() };

        return new PackageEventSetProvider(pkg);
    }

    private static class SampleTimeProvider 
            extends AbstractService implements PhysicalTimeProvider {

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
