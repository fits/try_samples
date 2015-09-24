package sample;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisPoolFactory implements ObjectFactory {
    private final List<String> ignoreProperties =
            Arrays.asList("factory", "auth", "scope", "singleton");

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {

        return (obj instanceof Reference)? createPool((Reference) obj): null;
    }

    private JedisPool createPool(Reference ref) throws Exception {
        JedisPoolBuilder builder = new JedisPoolBuilder();

        for (Enumeration<RefAddr> em = ref.getAll(); em.hasMoreElements();) {
            RefAddr ra = em.nextElement();

            String name = ra.getType();

            if (!ignoreProperties.contains(name)) {
                BeanUtils.setProperty(builder, name, ra.getContent());
            }
        }

        return builder.build();
    }

    public class JedisPoolBuilder {
        private JedisPoolConfig poolConfig = new JedisPoolConfig();

        private String host = Protocol.DEFAULT_HOST;
        private int port = Protocol.DEFAULT_PORT;
        private int timeout = Protocol.DEFAULT_TIMEOUT;

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public JedisPoolConfig getPoolConfig() {
            return poolConfig;
        }

        public JedisPool build() {
            return new JedisPool(poolConfig, host, port, timeout);
        }
    }
}
