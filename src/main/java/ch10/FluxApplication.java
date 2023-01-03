package ch10;

import ch9.MonoApplication;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class FluxApplication {

    @GetMapping("/event/{id}")
    Mono<List<Event>> hello(@PathVariable long id) {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Mono.just(list);
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        Flux<Event> es = Flux.<Event, Long>generate(() -> 1L, (id, sink) -> {
            sink.next(new Event(id, "value" + id));
            return id +1;
        });

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        return Flux.zip(es, interval).map(tu -> tu.getT1());
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }

    public static void main(String[] args) {
        SpringApplication.run(FluxApplication.class, args);
    }


}
