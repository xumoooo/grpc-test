package com.unitedtrader.finderby.test.grpc.server;

import com.google.common.base.Preconditions;
import com.unitedtrader.derby.test.grpc.MonetaryAmount;
import com.unitedtrader.derby.test.grpc.TestServiceGrpc;
import com.unitedtrader.derby.test.grpc.Ticker;
import io.grpc.stub.StreamObserver;

public class TestServiceImpl implements TestServiceGrpc.TestService {

    @Override
    public void getPrice(Ticker request, StreamObserver<MonetaryAmount> responseObserver) {

        PriceProvider priceProvider = new PriceProvider(request.getSymbol());

        priceProvider.getUpdates()
                .subscribe(responseObserver::onNext);
    }

    @Override
    public StreamObserver<Ticker> getPriceWithCancellation(StreamObserver<MonetaryAmount> responseObserver) {
        return new ProviderAdapter(responseObserver);
    }

    private final static class ProviderAdapter implements StreamObserver<Ticker> {

        private final StreamObserver<MonetaryAmount> responseObserver;

        private volatile boolean initialized = false;
        private volatile PriceProvider priceProvider;

        private ProviderAdapter(StreamObserver<MonetaryAmount> responseObserver) {
            this.responseObserver = responseObserver;
        }

        @Override
        public void onNext(Ticker ticker) {
            Preconditions.checkState(!initialized);
            initialized = true;

            priceProvider = new PriceProvider(ticker.getSymbol());
            priceProvider.getUpdates()
                    .subscribe(responseObserver::onNext);
        }

        @Override
        public void onError(Throwable throwable) {
            // ???
        }

        @Override
        public void onCompleted() {
            responseObserver.onCompleted();
            if(priceProvider != null) {
                priceProvider.close();
            }
        }
    }
}
