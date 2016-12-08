package sample;

import org.qi4j.api.concern.ConcernOf;

public class SampleConcern extends ConcernOf<Sample> implements Sample {
    @Override
    public String call() {
        return "<<< " + next.call() + " >>>";
    }
}
