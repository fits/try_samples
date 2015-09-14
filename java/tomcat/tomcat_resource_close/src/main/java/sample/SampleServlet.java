package sample;

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

        connectToData();

        res.getWriter().println("ok");
    }

    private void connectToData() {
        try {
            Context ctx = new InitialContext();

            DataBean bean = (DataBean)ctx.lookup("java:comp/env/sample/Data");

            System.out.println("info: " + bean.getInfo());

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
