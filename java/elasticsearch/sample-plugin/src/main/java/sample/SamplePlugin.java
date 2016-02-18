package sample;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;
import java.util.Collections;

public class SamplePlugin extends Plugin {
    @Override
    public String name() {
        return "sample-plugin";
    }

    @Override
    public String description() {
        return "Plugin Sample";
    }

    @Override
    public Collection<Module> nodeModules() {
        System.out.println("*** nodeModules");

        return Collections.<Module>singletonList(new SampleModule());
    }
}
