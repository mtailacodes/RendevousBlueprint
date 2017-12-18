package com.mtailacodes.blueprintrendevouz.Util;

import org.reactivestreams.Publisher;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by matthewtaila on 12/10/17.
 */
public class RxBus {

    public RxBus() {
    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

}