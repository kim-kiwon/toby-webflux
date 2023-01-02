package ch4;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@EnableAsync
@Slf4j
@SpringBootApplication
public class DeferredResultApplication {

    @RestController
    public static class MyController {
        Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();

        // 아래 코드를 통해 클라이언트가 풀링하는 API
        @GetMapping("/dr")
        public DeferredResult<String> deferredResult() {
            DeferredResult<String> dr = new DeferredResult<>();
            results.add(dr);
            return dr;
        }

        @GetMapping("/dr/count")
        public String drCount() {
            return String.valueOf(results.size());
        }

        // 외부에서 이벤트 발생시키는 API
        @GetMapping("/dr/event")
        public String drEvent(String msg) {
            for (DeferredResult<String> dr :results) {
                dr.setResult("Hello " + msg); // 이 때 결과가 세팅되며, 이 객체를 풀링하고 있는 곳에선 결과를 받을수있음
                results.remove(dr);
            }
            return "OK";
        }

    }


    public static void main(String[] args) {
        SpringApplication.run(DeferredResultApplication.class, args);
    }
}