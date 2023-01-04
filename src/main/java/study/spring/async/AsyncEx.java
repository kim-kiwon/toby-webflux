package study.spring.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Configuration
public class AsyncEx {
//    @Bean
//    ThreadPoolTaskExecutor tp() {
//        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
//        te.setCorePoolSize(10); // 1. 초기 스레드풀 크기
//        te.setMaxPoolSize(100); // 3. 큐가 꽉차면 확장할 풀 크기
//        te.setQueueCapacity(200); // 2. 초기 스레드풀이 꽉차면 들어오는 큐 크기
//
//        return te;
//    }

    @Service
    public static class AsyncService {
        @Async
        public void returnVoid() throws InterruptedException {
            log.info("in_void_async_method");
            Thread.sleep(2000);
        }

        @Async
        public Future<String> returnFuture() throws InterruptedException {
            log.info("in_future_async_method");
            Thread.sleep(2000);
            return new AsyncResult<>("future_async_end");
        }

        @Async
        public ListenableFuture<String> returnListenableFuture() throws InterruptedException {
            log.info("in_listenableFuture_async_method");
            Thread.sleep(2000);
            return new AsyncResult<>("listenableFuture_async_end");
        }

        @Async
        public CompletableFuture returnCompletableFuture() throws InterruptedException {
            log.info("in_listenableFuture_async_method");
            Thread.sleep(2000);
            return new AsyncResult<>("completableFuture_async_end").completable();
        }
    }

    @RestController
    public static class MyController {
        @Autowired
        AsyncService asyncService;

        @GetMapping("/void")
        public void voidController() throws InterruptedException {
            log.info(String.valueOf(asyncService.getClass()));
            log.info("pos1");
            asyncService.returnVoid();
            log.info("pos2");
        }

        @GetMapping("/future")
        public void futureController() throws InterruptedException, ExecutionException {
            log.info(String.valueOf(asyncService.getClass()));
            log.info("pos1");
            Future<String> f = asyncService.returnFuture();
            log.info("pos2");
            log.info("{}", f.get());
            log.info("pos3");
        }

        @GetMapping("/listenableFuture")
        public void listenableFutureController() throws InterruptedException, ExecutionException {
            log.info(String.valueOf(asyncService.getClass()));
            log.info("pos1");
            ListenableFuture<String> lf = asyncService.returnListenableFuture();
            log.info("pos2");
            lf.addCallback(s -> log.info("{}", s), e -> log.info("{}", e.getMessage()));
            log.info("pos3");
        }

        @GetMapping("/completableFuture")
        public void completableFutureController() throws InterruptedException, ExecutionException {
            log.info(String.valueOf(asyncService.getClass()));
            log.info("pos1");
            CompletableFuture<String> cf = asyncService.returnCompletableFuture();
            log.info("pos2");
            cf.thenAccept(p -> log.info("{}", p));
            log.info("pos3");
        }
    }
}
