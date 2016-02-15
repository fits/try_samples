package sample;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;

import java.util.Optional;

public class SampleSink extends AbstractSink implements Configurable {
	@Override
	public void configure(Context context) {
		System.out.println("*** configure");
	}

	@Override
	public Status process() throws EventDeliveryException {
		Channel ch = getChannel();
		Transaction tr = ch.getTransaction();

		try {
			tr.begin();

			Optional.ofNullable(ch.take()).map(this::processEvent);

			tr.commit();

			return Status.READY;
		} catch (Throwable e) {
			tr.rollback();

			if (e instanceof Error) {
				throw e;
			}

			return Status.BACKOFF;
		} finally {
			tr.close();
		}
	}

	private Void processEvent(Event ev) {
		System.out.println("*** processEvent");
		System.out.println(ev);

		return null;
	}
}
