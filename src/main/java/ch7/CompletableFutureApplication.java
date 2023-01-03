package ch7;

import io.netty.channel.nio.NioEventLoopGroup;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

@SuppressWarnings("deprecation")
@SpringBootApplication
@EnableAsync
@Slf4j
public class CompletableFutureApplication {

    @RestController
    public static class MyController {
        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @Autowired
        MyService myService;

        static final String URL1 = "http://localhost:8081/service?req={req}";
        static final String URL2 = "http://localhost:8081/service2?req={req}";

        @GetMapping("/rest")
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            // AsyncRestTemplate은 스프링 독자적인 ListenableFuture를 반환.
            // toCF 메서드로 ListeanbleFuture를 자바표준인 CompletableFuture로 변경.
            // CompletableFuture는 체인으로 연쇄 비동기 호출을 지원한다.
            toCF(rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx))
                .thenCompose(s -> toCF(rt.getForEntity("http://localhost:8081/service2?req={req}", String.class, s.getBody())))
                .thenCompose(s -> toCF(myService.work(s.getBody())))
                .thenAccept(s -> dr.setResult(s))
                .exceptionally(e -> {
                    dr.setErrorResult(e.getMessage());
                    return null;
                });

            return dr;
        }

        // ListenableFuture를 CompletableFuture로 변경
        // CompletableFuture 완료/예외 콜백에 CompletableFuture의 메서드들을 연동시키면된다.
        <T> CompletableFuture<T> toCF(ListenableFuture<T> lf) {
            CompletableFuture<T> cf = new CompletableFuture<>();
            lf.addCallback(s -> cf.complete(s), e -> cf.completeExceptionally(e));
            return cf;
        }
    }

    @Service
    public static class MyService {
        @Async
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork");
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);
        te.setMaxPoolSize(1);
        te.initialize();
        return te;
    }

    public static void main(String[] args) {
        SpringApplication.run(CompletableFutureApplication.class, args);
    }
}