package com.unitedtrader.finderby.test.grpc.server;

import com.unitedtrader.derby.test.grpc.Currency;
import com.unitedtrader.derby.test.grpc.MonetaryAmount;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PriceProvider {

    private final String symbol;

    private final Subscription subscription;

    private final Subject<MonetaryAmount, MonetaryAmount> updates = new SerializedSubject<>(PublishSubject.create());

    PriceProvider(String symbol) {
        this.symbol = symbol;

        Random random = new Random();

        Currency currency = randomCurrency();

        subscription = Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(time -> random.nextLong())
                .map(value -> MonetaryAmount.newBuilder().setCurrency(currency).setValue(value).build())
                .subscribe(updates);
    }

    public Observable<MonetaryAmount> getUpdates() {
        return updates;
    }

    public void close() {
        subscription.unsubscribe();
    }

    private static Currency randomCurrency() {
        if (new Random().nextBoolean()) {
            return Currency.USD;
        }

        return Currency.RUB;
    }
}
