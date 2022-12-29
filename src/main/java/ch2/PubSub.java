package ch2;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

/**
 * Publisher -> [Data1] -> op1 -> [Data2] -> op2 -> Subscriber
 * 스트림의 방식과 동일하다
 * - map (d1 -> f -> d2)
 *    pub -> [Data1] -> mapPub -> [Data2] -> logSub
 */

@Slf4j
public class PubSub {

	public static void main(String[] args) {
		Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
		Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
		// Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
		// Publisher<Integer> map2Pub = mapPub(mapPub, s -> s * 10);
		// Publisher<Integer> sumPub = sumPub(pub);
		mapPub.subscribe(logSub());
	}

	// List를 받아 데이터를 통지하는 Publisher
	private static Publisher<Integer> iterPub(List<Integer> iter) {
		return new Publisher<Integer>() {
			@Override
			public void subscribe(Subscriber<? super Integer> sub) {
				sub.onSubscribe(new Subscription() {
					@Override
					public void request(long n) {
						try {
							iter.forEach(s -> sub.onNext(s));
							sub.onComplete(); // 데이터를 다 줬다면 onComplete 콜백호출
						} catch (Throwable t) {
							sub.onError(t); // 에러가 발생시 onError 콜백호출
						}

					}

					@Override
					public void cancel() {

					}
				});
			}
		};
	}

	private static <T> Subscriber<T> logSub() {
		return new Subscriber<T>() {
			@Override
			public void onSubscribe(Subscription s) {
				log.debug("onSubscribe");
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(T i) {
				log.debug("onNext:{}", i);
			}

			@Override
			public void onError(Throwable t) {
				log.debug("onError:{}", t);
			}

			@Override
			public void onComplete() {
				log.debug("onComplete");
			}
		};
	}

	// private static <T> Publisher<T> mapPub(Publisher<T> pub, Function<T, T> f) {
	// 	return new Publisher<T>() {
	// 		// Subscriber를 받고. 그 Subscriber로 다른 요청은 다 전달하지만 onNext에는 부가기능 추가한 Subscriber 구현.
	// 		// 구현한 Subscriber로 Publisher를 subscribe
	// 		@Override
	// 		public void subscribe(Subscriber<? super T> sub) { // sub: 최하단 Subscriber
	// 			pub.subscribe(new DelegateSub<T>(sub) {
	// 				@Override
	// 				public void onNext(T i) {
	// 					sub.onNext(f.apply(i));
	// 				}
	// 			});
	// 		}
	// 	};
	// }

	// private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
	// 	return new Publisher<Integer>() {
	// 		@Override
	// 		public void subscribe(Subscriber<? super Integer> sub) { // sub: 최하단 Subscriber
	// 			pub.subscribe(new DelegateSub(sub) {
	// 				int sum = 0;
	//
	// 				@Override
	// 				public void onNext(Integer i) {
	// 					sum += i;
	// 				}
	//
	// 				@Override
	// 				public void onComplete() {
	// 					sub.onNext(sum);
	// 					sub.onComplete();
	// 				}
	// 			});
	// 		}
	// 	};
	// }

	// T -> R 로 타입 변경
	private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
		return new Publisher<R>() {
			@Override
			public void subscribe(Subscriber<? super R> sub) { // sub: 최하단 Subscriber
				pub.subscribe(new DelegateSub<T>(sub) {
					@Override
					public void onNext(T i) {
						sub.onNext(f.apply(i));
					}
				});
			}
		};
	}
}
