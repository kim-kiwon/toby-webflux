package ch2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSub<T> implements Subscriber<T> {
	Subscriber originalSub; // 최하단 Subscriber 필드로 가짐. 원래거 호출하기 위해.

	public DelegateSub(Subscriber originalSub) {
		this.originalSub = originalSub;
	}

	@Override
	public void onSubscribe(Subscription s) {
		originalSub.onSubscribe(s);
	}

	@Override
	public void onNext(T i) {
		originalSub.onNext(i);
	}

	@Override
	public void onError(Throwable t) {
		originalSub.onError(t);
	}

	@Override
	public void onComplete() {
		originalSub.onComplete();
	}
}
