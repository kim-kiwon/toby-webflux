package ch7;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);

        // CompletableFuture는 CompletionStage르 구현.
        // CompletionStage는 하나의 작업을 비동기로 이어서 호출할 수 있게 해줌.
        CompletableFuture
            .supplyAsync(() -> {
                log.info("runAsync");
                return 1;
            }, es)
            .thenCompose(s -> {
                log.info("thenApply {}", s);
                return CompletableFuture.completedFuture(s + 1);
            })
            .thenApplyAsync(s -> {
                log.info("thenRun {}", s);
                return s + 1;
            }, es)
            .exceptionally(e -> -10)
            .thenAccept(s2 -> log.info("thenRun {}", s2));
        log.info("exit");

        ForkJoinPool.commonPool().shutdown(); // java7 부터는 ForkJoinPool의 coomPool 에서 스레드하나 받음
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS); //
    }
}
