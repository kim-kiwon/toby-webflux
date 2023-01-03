package ch5;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0); // 여러 스레드에서 접근시 RaceCondition 방지하는 클래스

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101); // await 한 스레드가 101개가되는 순간 한번에 진행

        for(int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread {}", idx);

                barrier.await(); // 스레드 정지

                StopWatch sw = new StopWatch();
                sw.start();

                String res = rt.getForObject(url, String.class, idx);

                sw.stop();
                log.info("Elapsed: {} {} / {} ", idx, sw.getTotalTimeSeconds(), res);
                return null; // runnable 구현한 lambda 식 내에 Exception 던질 수 없음. 반환값 만들어서 Callable로 변경
            });
        }

        barrier.await(); // 메인 스레드 정지
        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }
}