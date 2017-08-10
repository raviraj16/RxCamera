package com.rx.camera;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Ravi Raj Priyadarshi on 10-08-2017.
 */

public class RxEventBus {
    private static RxEventBus rxEventBus;
    private final Subject<Object> subscriberSubjects = PublishSubject.create();

    private RxEventBus() {

    }

    public static RxEventBus getEventBus() {
        if (rxEventBus == null) {
            rxEventBus = new RxEventBus();
        }
        return rxEventBus;
    }

    //

    /**
     * Send any object to bus i.e. emits data to subscribers
     * Use "RxEventBus.getEventBus().sendToBus(new RxPojo(data, resultCode))" in onActivityResult of your Activity ;
     * @param o
     */
    public void sendToBus(Object o) {
        subscriberSubjects.onNext(o);
    }

    //get observable and subscribe anywhere
    public Observable<Object> getObservables() {
        return subscriberSubjects;
    }
}
