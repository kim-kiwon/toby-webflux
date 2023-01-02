package ch4;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureEx {
    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        // Callable: 수행할 비동기 작업
        // SuccessCallback : 수행완료시 호출될 코드
        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        // Callable과 onSuccess를 구현한 SuccessCallback을 넣어줌
        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        },  s-> System.out.println("Result: " + s),
            e-> System.out.println("Error: " + e.getMessage()));

        es.execute(f);
        es.shutdown();


//        // Future는 비동기로 다른 스레드에서 실행되는 메서드의 반환값을. 호출한 곳에서 사용할 수 있게 해준다.
//        Future<String> f =es.submit(() -> {
//            Thread.sleep(2000);
//
//            return "Hello";
//        });
//
//        System.out.println(f.get()); // future의 get은 블로킹이다.
//        log.info("Exit");
//
//        FutureTask<String> f = new FutureTask<String>(() -> {
//            Thread.sleep(2000);
//            log.info("Async");
//            return "Hello";
//        }) {
//            @Override
//            protected void done() {
//                try {
//                    System.out.println(get());
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };
    }
}
