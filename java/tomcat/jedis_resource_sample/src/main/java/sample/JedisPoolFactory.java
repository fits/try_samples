package sample;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.naming.ResourceRef;
import org.apache.naming.factory.Constants;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 *
 */
public class JedisPoolFactory implements ObjectFactory {
    private final List<String> ignoreProperties =
            Arrays.asList(Constants.FACTORY, "auth", "scope", "singleton");

    /**
     *
     * @param obj
     * @param name
     * @param nameCtx
     * @param environment
     * @return
     * @throws Exception
     */
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {

        return (obj instanceof ResourceRef)? createPool((ResourceRef) obj): null;
    }

    /**
     *
     * @param ref
     * @return
     * @throws Exception
     */
    private JedisPool createPool(ResourceRef ref) throws Exception {
        JedisPoolBuilder builder = new JedisPoolBuilder();

        for (Enumeration<RefAddr> em = ref.getAll(); em.hasMoreElements();) {
            RefAddr ra = em.nextElement();
            String propName = ra.getType();

            if (!ignoreProperties.contains(propName)) {
                BeanUtils.setProperty(builder, propName, ra.getContent());
            }
        }

        return builder.build();
    }

    /**
     *
     */
    public class JedisPoolBuilder {
        private JedisPoolConfig poolConfig = new JedisPoolConfig();

        private Optional<URI> uriOpt = Optional.empty();

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

        public void setUri(String uri) throws URISyntaxException {
            this.uriOpt = Optional.of(new URI(uri));
        }

        public JedisPoolConfig getPoolConfig() {
            return poolConfig;
        }

        public JedisPool build() {
            return uriOpt.map(uri -> new JedisPool(poolConfig, uri, timeout))
                    .orElseGet(() -> new JedisPool(poolConfig, host, port, timeout));
        }
    }
}
