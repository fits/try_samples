package sample;

import com.sun.tools.attach.VirtualMachine;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;

public class ThreadStat {
    private static final long DEFAULT_INTERVAL = 1000;

    public static void main(String... args) throws Exception {
        if (args.length < 1) {
            System.out.println("<PID> [<interval(ms)>]");
            return;
        }

        long interval = (args.length > 1)? Long.parseLong(args[1]): DEFAULT_INTERVAL;

        processThreadMXBean(args[0], b -> dumpThread(interval, b));
    }

    private static void processThreadMXBean(String pid, Consumer<ThreadMXBean> func) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(pid);

        try {
            String jmxUri = vm.startLocalManagementAgent();

            try(JMXConnector con = JMXConnectorFactory.connect(new JMXServiceURL(jmxUri))) {
                MBeanServerConnection server = con.getMBeanServerConnection();

                ThreadMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                        ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);

                func.accept(bean);
            }
        } finally {
            vm.detach();
        }
    }

    private static void dumpThread(long interval, ThreadMXBean bean) {

        Map<Long, ThreadData> thMap = calculateCpuUsage(bean, () -> {
            try {
                Thread.sleep(interval);
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }
            return null;
        });

        LongToDoubleFunction cpuUsage = id ->
                thMap.containsKey(id)? thMap.get(id).cpuUsage(): -1;

        Arrays.stream(bean.dumpAllThreads(false, false)).forEach(info -> {

            System.out.printf("\"%s\" id=%d cpu=%.3f %s \n",
                    info.getThreadName(), info.getThreadId(), 
                    cpuUsage.applyAsDouble(info.getThreadId()),
                    info.getThreadState());

            for (StackTraceElement st : info.getStackTrace()) {
                System.out.printf("    at %s \n", st);
            }

            System.out.println();
        });
    }

    private static Map<Long, ThreadData> calculateCpuUsage(ThreadMXBean bean, Supplier<?> waitFunc) {

        List<ThreadData> list = Arrays.stream(bean.getAllThreadIds())
            .mapToObj(id -> new ThreadData(id, bean::getThreadCpuTime).start())
            .collect(Collectors.toList());

        waitFunc.get();

        return list.stream()
            .map(ThreadData::stop)
            .collect(Collectors.toMap(ThreadData::getId, UnaryOperator.identity()));
    }

    private static class ThreadData {
        private final long id;
        private final LongUnaryOperator cpuTimeGetter;

        private Time processTime;
        private Time cpuTime;

        ThreadData(long id, LongUnaryOperator cpuTimeGetter) {
            this.id = id;
            this.cpuTimeGetter = cpuTimeGetter;
        }

        long getId() {
            return id;
        }

        double cpuUsage() {
            if (processTime == null || cpuTime == null) {
                return -1;
            }

            double cpuDiff = cpuTime.diff();

            return (cpuDiff < 0)? -1: cpuDiff / processTime.diff();
        }

        ThreadData start() {
            processTime = new Time(System.nanoTime());
            cpuTime = new Time(cpuTimeGetter.applyAsLong(id));

            return this;
        }

        ThreadData stop() {
            cpuTime.end = cpuTimeGetter.applyAsLong(id);
            processTime.end = System.nanoTime();

            return this;
        }
    }

    private static class Time {
        private long start = -1;
        private long end = -1;

        Time(long start) {
            this.start = start;
        }

        long diff() {
            return (start < 0 || end < 0)? -1: end - start;
        }
    }
}
