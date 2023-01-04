package study.java;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        log.info("pos1");

        Future<String> future = executor.submit(() -> {
            log.info("in_future");
            Thread.sleep(2000);
            return "end";
        });

        log.info("pos2");

        log.info("{}",future.get());

        log.info("pos3");

        executor.shutdown();
    }
}
