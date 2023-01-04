package study.java;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureTaskCallbackEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newSingleThreadExecutor();

        FutureTask<String> f = new FutureTask<String>(() -> {
            log.info("in_futureTask");
            Thread.sleep(2000);
            return "futureTask_end";
        }) {
            @Override
            protected void done() {
                try {
                    log.info("{}", get());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        log.info("pos1");

        es.execute(f);

        log.info("pos2");

        log.info("pos3");

        es.shutdown();
    }
}
