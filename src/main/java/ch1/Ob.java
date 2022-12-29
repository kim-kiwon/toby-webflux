package ch1;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Ob {

    // 데이터를 발행하는 주체. Observable 별도 스레드로 구성.
    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for(int i=1;i <=10; i++) {
                setChanged();
                notifyObservers(i);     // push
                // int i = it.next();   // pull
            }
        }
    }

    // 기존은 이미 존재하는 데이터를 땡겨오는 방식 > pull
    // 옵저버 패턴은 데이터를 발행하고 넘겨주는 방식 > push
    //  push 방식은 발행과 수신해서 사용하는 스레드를. 별개의 스레드에서 동작하도록 매우 손쉽게 만들 수 있다.
    public static void main(String[] args) {
        // 데이터를 구독하는 주체. Observer
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);

        System.out.println(Thread.currentThread().getName() + " EXIT");
        es.shutdown();
    }
}
