package sample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/sample")
public class SampleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        accessToRedis();

        res.getWriter().println("ok");
    }

    private void accessToRedis() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml");

        @SuppressWarnings("unchecked")
        RedisTemplate<Object, Object> redisTemplate = ctx.getBean(RedisTemplate.class);

        JedisConnectionFactory jcf = (JedisConnectionFactory)redisTemplate.getConnectionFactory();

        System.out.println("maxTotal: " + jcf.getPoolConfig().getMaxTotal());
        System.out.println("maxIdle: " + jcf.getPoolConfig().getMaxIdle());

        System.out.println("keys: " + redisTemplate.keys("a*"));
    }
}
