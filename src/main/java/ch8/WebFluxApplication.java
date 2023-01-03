package ch8;

import io.netty.channel.nio.NioEventLoopGroup;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
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
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SuppressWarnings("deprecation")
@SpringBootApplication
@RestController
@Slf4j
public class WebFluxApplication {
    static final String URL1 = "http://localhost:8081/service?req={req}";
    static final String URL2 = "http://localhost:8081/service2?req={req}";

    WebClient client = WebClient.create();

    @Autowired
    MyService myService;

    @GetMapping("/rest")
    public Mono<String> rest(int idx) {
        return client.get().uri(URL1, idx).exchange() // Mono<ClientResponse>
            .flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
            .flatMap(res1 -> client.get().uri(URL2, res1).exchange()) // Mono<ClientResponse>
            .flatMap(c -> c.bodyToMono(String.class)); // Mono<String>
    }

    @Service
    public static class MyService {
        @Async
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(WebFluxApplication.class, args);
    }
}