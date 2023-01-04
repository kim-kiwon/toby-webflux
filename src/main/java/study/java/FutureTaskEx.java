package study.java;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureTaskEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();

        FutureTask<String> f = new FutureTask<String>(() -> {
            log.info("in_futureTask");
            Thread.sleep(2000);
            return "futureTask_end";
        });

        log.info("pos1");

        es.execute(f);

        log.info("pos2");

        log.info("{}", f.get());

        log.info("pos3");

        es.shutdown();
    }
}
