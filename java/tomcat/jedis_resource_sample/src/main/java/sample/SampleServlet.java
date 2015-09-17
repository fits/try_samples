package sample;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
        try (Jedis jedis = getPool().getResource()) {
            System.out.println(jedis.keys("*"));
        }
    }

    private JedisPool getPool() {
        try {
            Context ctx = new InitialContext();
            return (JedisPool) ctx.lookup("java:comp/env/redis/Pool");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
