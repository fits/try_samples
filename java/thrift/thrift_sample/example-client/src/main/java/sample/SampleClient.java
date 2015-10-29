package sample;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class SampleClient {
    public static void main(String... args) throws Exception {

        try (TTransport transport = new TSocket("localhost", 9090)) {
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);

            SampleService.Client client = new SampleService.Client(protocol);

            Data d = new Data();
            d.setName("sample" + System.currentTimeMillis());

            long value = (long)(Math.random() * 100);

            d.setValue(value);

            client.add(d);

            client.findAll().forEach(System.out::println);
        }
    }
}
