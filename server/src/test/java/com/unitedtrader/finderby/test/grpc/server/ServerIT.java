package com.unitedtrader.finderby.test.grpc.server;

import com.unitedtrader.derby.test.grpc.AuthToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServerIT {

    private Server server;
    private Client client;

    @Before
    public void initialize() throws Exception {
        server = new Server();
        server.start();

        client = new Client("localhost", Server.PORT);
    }

    @After
    public void cleanUp() {
        server.stop();
    }

    @Test(expected = io.grpc.StatusRuntimeException.class)
    public void authenticateWithWrongPassword() {
        AuthToken authToken = client.authenticate("anonim", "");
    }

    @Test
    public void authenticateSuccess() {
        AuthToken authToken = client.authenticate(AuthenticationServiceImpl.LOGIN, AuthenticationServiceImpl.PASSWORD);

        assertThat(authToken, notNullValue());
        assertThat(authToken.getToken(), notNullValue());
    }
}
