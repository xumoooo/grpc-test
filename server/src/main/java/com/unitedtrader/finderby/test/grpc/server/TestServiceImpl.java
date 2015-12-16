package com.unitedtrader.finderby.test.grpc.server;

import com.unitedtrader.derby.test.grpc.MonetaryAmount;
import com.unitedtrader.derby.test.grpc.TestServiceGrpc;
import com.unitedtrader.derby.test.grpc.Ticker;
import io.grpc.stub.StreamObserver;

public class TestServiceImpl implements TestServiceGrpc.TestService {

    @Override
    public void getPrice(Ticker request, StreamObserver<MonetaryAmount> responseObserver) {
        throw new RuntimeException("not implemented"); // todo: implement
    }
}
