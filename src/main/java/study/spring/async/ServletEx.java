package study.spring.async;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet(urlPatterns = "/hello", asyncSupported = true)
public class ServletEx extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        final AsyncContext asyncContext = req.startAsync(); // AsyncContext 생성
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                try {
                    response.getWriter().println("HELLO");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("complete response");
                asyncContext.complete();
            }
        }).start();
        log.info("doGet return");
    }

}
