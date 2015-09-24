package sample;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Enumeration;
import java.util.Hashtable;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public class SimpleJedisPoolFactory implements ObjectFactory {
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {
        return (obj instanceof Reference)? createPool((Reference) obj): null;
    }

    private JedisPool createPool(Reference ref) throws Exception {
        String host = Protocol.DEFAULT_HOST;
        int port = Protocol.DEFAULT_PORT;

        for (Enumeration<RefAddr> em = ref.getAll(); em.hasMoreElements();) {
            RefAddr ra = em.nextElement();

            String name = ra.getType();
            String value = (String)ra.getContent();

            switch (name) {
                case "host":
                    host = value;
                    break;
                case "port":
                    port = Integer.parseInt(value);
                    break;
            }
        }

        return new JedisPool(host, port);
    }
}
