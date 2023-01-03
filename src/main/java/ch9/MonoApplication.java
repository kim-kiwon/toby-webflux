package ch9;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@Slf4j
public class MonoApplication {
    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        String msg = generateHello();
        Mono<String> m = Mono.just(msg).log();
        String msg2 = m.block(); // 에러발생
        log.info("pos2 " + msg2);
        return m;
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }

    public static void main(String[] args) {
        SpringApplication.run(MonoApplication.class, args);
    }
}
