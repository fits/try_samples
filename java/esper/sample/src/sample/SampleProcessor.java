
package sample;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.EventBean;

import sample.event.SampleEvent;

public class SampleProcessor {

	public static void main(String[] args) {
	
		Configuration config = new Configuration();
		config.addEventTypeAlias("SampleEvent", SampleEvent.class);
	
		EPServiceProvider serv = EPServiceProviderManager.getDefaultProvider(config);

		EPStatement st = serv.getEPAdministrator().createEPL("select * from SampleEvent(point >= 5)");
		//ˆÈ‰º‚Å‚à‰Â
		//EPStatement st = serv.getEPAdministrator().createEPL("select * from SampleEvent where point >= 5");

		st.addListener(new UpdateListener() {
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {

				if (newEvents != null && newEvents.length > 0) {
					System.out.println("event : " + newEvents[0].get("name") + ", " + newEvents[0].get("point"));
				}

			}
		});

		//ƒCƒxƒ“ƒg‚Ì”­¶
		for (int i = 0; i < 10; i++) {
			int val = (int)(Math.random() * 10);

			serv.getEPRuntime().sendEvent(new SampleEvent("test" + i, val));
		}
	}

}
