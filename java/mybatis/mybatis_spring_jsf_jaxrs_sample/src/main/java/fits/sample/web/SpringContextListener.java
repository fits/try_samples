package fits.sample.web;

import javax.servlet.annotation.WebListener;

@WebListener
//public class SpringContextListener extends org.springframework.web.context.ContextLoaderListener {
public class SpringContextListener extends org.jboss.resteasy.plugins.spring.SpringContextLoaderListener {
}
