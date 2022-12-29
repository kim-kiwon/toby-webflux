package ch3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

@Slf4j
public class SchedulerEx {
	public static void main(String[] args) {
		Publisher<Integer> pub = sub -> {
			sub.onSubscribe(new Subscription() {
				@Override
				public void request(long n) {
					log.debug("request()");
					// 직접 데이터를 전달하도록 설정
					sub.onNext(1);
					sub.onNext(2);
					sub.onNext(3);
					sub.onNext(4);
					sub.onNext(5);
					sub.onComplete();
				}

				@Override
				public void cancel() {

				}
			});
		};
		// pub

//		Publisher -> subOnPub -> Subscriber
		Publisher<Integer> subOnPub = sub -> {
			ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
				@Override
				public String getThreadNamePrefix() {
					return "subOn-";
				}
			}); // 한번에 하나의 스레드 주는 스레드풀
			es.execute(() -> pub.subscribe(sub)); // 중간에서 Publisher의 subscribe함수를 스레드로 실행하도록 변경해줌.
		};

		Publisher<Integer> pubOnPub = sub -> {
			subOnPub.subscribe(new Subscriber<Integer>() {
				ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
					@Override
					public String getThreadNamePrefix() {
						return "pubOn-";
					}
				});

				@Override
				public void onSubscribe(Subscription s) {
					sub.onSubscribe(s);
				}

				@Override
				public void onNext(Integer integer) {
					es.execute(() -> sub.onNext(integer));
				}

				@Override
				public void onError(Throwable t) {
					es.execute(() -> sub.onError(t));
					es.shutdown();
				}

				@Override
				public void onComplete() {
					es.execute(() -> sub.onComplete());
					es.shutdown();
				}
			});
		};


		// sub

		pubOnPub.subscribe(new Subscriber<Integer>() {
			@Override
			public void onSubscribe(Subscription s) {
				log.debug("onSubscribe");
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(Integer integer) {
				log.debug("onNext:{}", integer);
			}

			@Override
			public void onError(Throwable t) {
				log.debug("onError:{}", t);
			}

			@Override
			public void onComplete() {
				log.debug("onComplete");
			}
		});
		System.out.println("exit");
	}
}
