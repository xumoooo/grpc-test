package com.unitedtrader.finderby.test.grpc.server;

import com.unitedtrader.derby.test.grpc.AuthToken;
import com.unitedtrader.derby.test.grpc.AuthenticationData;
import com.unitedtrader.derby.test.grpc.AuthenticationServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class AuthenticationServiceImpl implements AuthenticationServiceGrpc.AuthenticationService {

    private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class.getName());

    final static String LOGIN = "A_TESTER";
    final static String PASSWORD = "123";
    public final static String TOKEN = "SECRET_TOKEN";


    @Override
    public void authenticate(AuthenticationData request, StreamObserver<AuthToken> responseObserver) {
        if (!LOGIN.equals(request.getEmailOrLogin()) || !PASSWORD.equals(request.getPassword())) {
            responseObserver.onError(new RuntimeException("unknown user"));
            logger.warning("can't authenticate user");
            return;
        }

        responseObserver.onNext(AuthToken.newBuilder().setToken(TOKEN).build());
        responseObserver.onCompleted();
    }
}
