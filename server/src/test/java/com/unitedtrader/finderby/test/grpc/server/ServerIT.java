package com.unitedtrader.finderby.test.grpc.server;

import com.google.common.base.Preconditions;
import com.unitedtrader.derby.test.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ServerIT {

    private Server server;
    private ManagedChannel clientChannel;

    @Before
    public void initialize() throws Exception {
        server = new Server();
        server.start();

        clientChannel = ManagedChannelBuilder.forAddress("localhost", Server.PORT)
                .usePlaintext(true)
                .build();
    }

    @After
    public void cleanUp() {
        server.stop();
    }

    @Test(expected = io.grpc.StatusRuntimeException.class)
    public void authenticateWithWrongPassword() {
        AuthToken authToken = AuthenticationServiceGrpc.newBlockingStub(clientChannel)
                .authenticate(AuthenticationData.newBuilder()
                        .setEmailOrLogin("anonim")
                        .setPassword("")
                        .build());
    }

    @Test
    public void authenticateSuccess() {
        AuthToken authToken = AuthenticationServiceGrpc.newBlockingStub(clientChannel)
                .authenticate(AuthenticationData.newBuilder()
                        .setEmailOrLogin(AuthenticationServiceImpl.LOGIN)
                        .setPassword(AuthenticationServiceImpl.PASSWORD)
                        .build());

        assertThat(authToken, notNullValue());
        assertThat(authToken.getToken(), notNullValue());
    }

    // @Test // infinite
    public void infiniteUpdates() {
        Ticker ticker = Ticker.newBuilder().setSymbol("RTS-3.16~RTS").build();
        Iterator<MonetaryAmount> iterator = TestServiceGrpc.newBlockingStub(clientChannel).getPrice(ticker);
        while(TestServiceGrpc.newBlockingStub(clientChannel).getPrice(ticker).hasNext()) {
            MonetaryAmount monetaryAmount = iterator.next();

            System.out.println("!!!" + monetaryAmount.getValue());
        }
    }

    @Test
    public void infiniteUpdatesWithCancellation() throws InterruptedException {
        Ticker ticker = Ticker.newBuilder().setSymbol("RTS-3.16~RTS").build();
        StreamObserverWrapper streamObserverWrapper = new StreamObserverWrapper();
        StreamObserver<Ticker> tickerStreamObserver = TestServiceGrpc.newStub(clientChannel).getPriceWithCancellation(streamObserverWrapper);

        tickerStreamObserver.onNext(ticker);

        Thread.sleep(5000);

        tickerStreamObserver.onCompleted();

        Thread.sleep(1000); // wait response from server

        assertThat(streamObserverWrapper.completed, is(true));
    }

    private final static class StreamObserverWrapper implements StreamObserver<MonetaryAmount> {

        private volatile boolean completed = false;

        @Override
        public void onNext(MonetaryAmount monetaryAmount) {
            Preconditions.checkState(!completed);

            System.out.println("onNext" + monetaryAmount.getValue());
        }

        @Override
        public void onError(Throwable throwable) {
            Preconditions.checkState(!completed);

            System.out.println("onError");
        }

        @Override
        public void onCompleted() {
            System.out.println("onComplete");
            completed = true;
        }
    }
}
