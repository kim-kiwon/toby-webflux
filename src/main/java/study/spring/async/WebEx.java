package study.spring.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
public class WebEx {
    @RestController
    public static class ReturnTestController {

        @GetMapping("/callable")
        public Callable<String> callable() {
            log.info("pos1");
            return (() -> {
                log.info("in_long_job");
                Thread.sleep(2000);
                return "end_long_job";
            });
        }

        @GetMapping("/deferredResult")
        public DeferredResult<String> deferredResult() {
            DeferredResult<String> dr = new DeferredResult<>(60000L); // 타임아웃 시간

            log.info("pos1");

            ForkJoinPool.commonPool().submit(() -> {
                log.info("in_long_job");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                dr.setResult("end_long_job");
            });

            return dr;
        };

    }
}
