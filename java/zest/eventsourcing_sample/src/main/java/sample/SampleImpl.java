package sample;

import org.qi4j.api.injection.scope.Service;
import sample.entity.Data;
import sample.entity.DataEntityFactoryService;

public class SampleImpl implements Sample {
    @Service
    private DataEntityFactoryService factory;

    @Override
    public String call() {
        System.out.println("factoryservice:" + factory);

        Data data = factory.create("sample", 1);

        data.addValue(4);

        return "sample:" + data + ", name=" + data.name() + ", value=" + data.value();
    }
}
