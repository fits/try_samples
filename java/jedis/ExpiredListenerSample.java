package sample;

import lombok.val;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.Executors;

public class ExpiredListenerSample {
    private final static String KEYEVENT_EXPIRED = "__keyevent@0__:expired";

    public static void main(String... args) {
        val jedis = new Jedis();

        val execService = Executors.newSingleThreadExecutor();

        val listener = new ExpiredListener();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            listener.unsubscribe();
            jedis.close();

            System.out.println("stop");
        }));

        execService.submit(() -> {
            System.out.println("start ...");

            jedis.subscribe(listener, KEYEVENT_EXPIRED);
        });

        execService.shutdown();
    }

    private static class ExpiredListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            System.out.println("ch:" + channel + ", msg:" + message);
        }
    }
}
