package ch4;

import org.springframework.boot.SpringApplication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;


@Slf4j
@SpringBootApplication
@EnableAsync // @Async로 메서드를 비동기적으로 실행하려면 컨테이너 클래스에 추가해주어야함.
public class ListenableFutureApplication {

    @Component
    public static class MyService {
        @Async // @Async를 추가하면 비동기적으로 작업을 수행해줌.
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>("Hello"); // AsyncResult: 비동기적인 작업을 수행할거다.
        }
//        @Async // @Async를 추가하면 비동기적으로 작업을 수행해줌.
//        public Future<String> hello() throws InterruptedException {
//            log.info("hello()");
//            Thread.sleep(2000);
//            return new AsyncResult<>("Hello"); // AsyncResult: 비동기적인 작업을 수행할거다.
//        }
    }

    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10); // 1. 초기 스레드풀 크기
        te.setMaxPoolSize(100); // 3. 큐가 꽉차면 확장할 풀 크기
        te.setQueueCapacity(200); // 2. 초기 스레드풀이 꽉차면 들어오는 큐 크기

        return te;
    }

    public static void main(String[] args) {
        try(ConfigurableApplicationContext c = SpringApplication.run(ListenableFutureApplication.class, args)) {

        }
    }

    @Autowired MyService myService;

    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            ListenableFuture<String> f = myService.hello();
            f.addCallback(s -> System.out.println(s), e-> System.out.println(e.getMessage()));
//            Future<String> f = myService.hello();
//            log.info("result: " + f.get());
            log.info("exit");
        };
    }

}
