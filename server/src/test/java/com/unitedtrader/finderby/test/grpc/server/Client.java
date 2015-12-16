package com.unitedtrader.finderby.test.grpc.server;

import com.unitedtrader.derby.test.grpc.AuthToken;
import com.unitedtrader.derby.test.grpc.AuthenticationData;
import com.unitedtrader.derby.test.grpc.AuthenticationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class Client {

    private final ManagedChannel channel;

    private final AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationService;

    public Client(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();
        authenticationService = AuthenticationServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public AuthToken authenticate(String login, String password) {
        return authenticationService.authenticate(AuthenticationData.newBuilder().setEmailOrLogin(login).setPassword(password).build());
    }
}
