package ch3;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class FluxScEx {

    public static void main(String[] args) throws InterruptedException {
//        Flux.range(1, 10)
//            .publishOn(Schedulers.newSingle("pub"))
//            .log()
//            .subscribeOn(Schedulers.newSingle("sub"))
//            .subscribe(System.out::println);

        Flux.interval(Duration.ofMillis(500))
            .subscribe(s->log.debug("onNext:{}", s));

        TimeUnit.SECONDS.sleep(10);
    }
}
