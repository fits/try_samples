package simple;

import hudson.Plugin;
import hudson.tasks.BuildStep;

public class PluginImpl extends Plugin {
	
    public void start() throws Exception {
    	BuildStep.PUBLISHERS.add(JavaNcssPublisher.DESCRIPTOR);
    }
}
