package sample.resource;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.InitializingBean;

public class InitializingBeanFactory implements ObjectFactory {
    private final List<String> ignoreProperties =
            Arrays.asList("factory", "auth", "scope", "singleton");

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {

        return (obj instanceof Reference)? createBean((Reference) obj): null;
    }

    private Object createBean(Reference ref) throws Exception {
        Object bean = loadClass(ref.getClassName()).newInstance();

        setProperties(bean, ref);

        ((InitializingBean)bean).afterPropertiesSet();

        return bean;
    }

    private void setProperties(Object bean, Reference ref)
            throws InvocationTargetException, IllegalAccessException {

        for (Enumeration<RefAddr> em = ref.getAll(); em.hasMoreElements();) {
            RefAddr ra = em.nextElement();

            String name = ra.getType();

            if (!ignoreProperties.contains(name)) {
                BeanUtils.setProperty(bean, name, ra.getContent());
            }
        }
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        return (loader == null)? Class.forName(className): loader.loadClass(className);
    }
}
