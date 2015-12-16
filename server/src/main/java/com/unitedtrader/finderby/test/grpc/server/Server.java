package com.unitedtrader.finderby.test.grpc.server;

import com.unitedtrader.derby.test.grpc.AuthenticationServiceGrpc;
import com.unitedtrader.derby.test.grpc.TestServiceGrpc;
import io.grpc.ServerBuilder;

import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public final static int PORT = 5050;

    private io.grpc.Server server;

    void start() throws Exception {
        server = ServerBuilder.forPort(PORT)
                .addService(AuthenticationServiceGrpc.bindService(new AuthenticationServiceImpl()))
                .addService(TestServiceGrpc.bindService(new TestServiceImpl()))
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Server.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
        server.blockUntilShutdown();
    }
}
