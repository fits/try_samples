package sample.counter.monitor;

import static com.lightbend.lagom.javadsl.api.Service.named;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;

public interface CounterMonitorService extends Service {
    @Override
    default Descriptor descriptor() {
        return named("counter-monitor");
    }
}
