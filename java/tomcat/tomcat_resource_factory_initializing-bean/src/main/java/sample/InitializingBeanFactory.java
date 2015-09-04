package sample;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.naming.ResourceRef;
import org.apache.naming.factory.Constants;
import org.springframework.beans.factory.InitializingBean;

public class InitializingBeanFactory implements ObjectFactory {
    private final List<String> ignoreProperties =
            Arrays.asList(Constants.FACTORY, "auth", "scope", "singleton");

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {

        return (obj instanceof ResourceRef)? createBean((ResourceRef) obj): null;
    }

    private Object createBean(ResourceRef ref) throws Exception {
        Object bean = loadClass(ref.getClassName()).newInstance();

        initBean(bean, ref.getAll());

        if (bean instanceof InitializingBean) {
            ((InitializingBean)bean).afterPropertiesSet();
        }

        return bean;
    }

    private void initBean(Object bean, Enumeration<RefAddr> em) throws Exception {
        while (em.hasMoreElements()) {
            RefAddr ra = em.nextElement();

            String propName = ra.getType();

            if (!ignoreProperties.contains(propName)) {

                if (!PropertyUtils.isWriteable(bean, propName)) {
                    throw new NamingException("property not found: " + propName);
                }

                BeanUtils.setProperty(bean, propName, ra.getContent());
            }
        }
    }

    private Class<?> loadClass(String className) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        return (loader == null)? Class.forName(className): loader.loadClass(className);
    }
}
