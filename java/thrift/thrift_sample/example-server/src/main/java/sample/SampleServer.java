package sample;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SampleServer {
    public static void main(String... args) throws Exception {
        SampleService.Processor<SampleHandler> processor =
                new SampleService.Processor<>(new SampleHandler());

        TServerTransport transport = new TServerSocket(9090);

        TServer server = new TThreadPoolServer(
                new TThreadPoolServer.Args(transport).processor(processor));

        System.out.println("start server ...");

        server.serve();
    }

    static class SampleHandler implements SampleService.Iface {
        private List<Data> list = new CopyOnWriteArrayList<>();

        public void add(Data data) throws org.apache.thrift.TException {
            list.add(data);
        }

        public List<Data> findAll() throws org.apache.thrift.TException {
            return list;
        }
    }
}
