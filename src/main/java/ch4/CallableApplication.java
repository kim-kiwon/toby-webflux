package ch4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@EnableAsync
@Slf4j
@SpringBootApplication
public class CallableApplication {
    @RestController
    public static class MyController {
        // Callable : 비동기 작업 메서드를 담고 있는 객체
//        @GetMapping("/async")
//        public Callable<String> async() throws InterruptedException {
//            log.info("callable");
//            return () -> {
//                log.info("async");
//                Thread.sleep(2000);
//                return "hello";
//            };
//        }

        @GetMapping("/callable")
        public String callable() throws InterruptedException {
            log.info("async");
            Thread.sleep(2000);
            return "hello";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(CallableApplication.class, args);
    }
}