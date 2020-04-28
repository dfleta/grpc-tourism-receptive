package org.elsmancs.grpc.ufos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.elsmancs.grpc.payment.PaymentClient;
import org.elsmancs.grpc.payment.PaymentServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UfosDispatcherTest {

    private UfosParkServer serverUfos = null;
    private PaymentServer serverPayment = null;

    @Before
    public void setup() throws Exception {
        
        serverUfos = new UfosParkServer();
        serverUfos.start();

        serverPayment = new PaymentServer();
        serverPayment.start();
    }

    @After
    public void tearDown() throws Exception {
        serverUfos.stop();
        serverPayment.stop();
    }

    @Test
    public void dispatch_test() throws Exception {

        UfosDispatcher dispatcher = new UfosDispatcher();
        dispatcher.dispatch("Rick", "111111111111");

        PaymentClient paymentClient = PaymentClient.init();
        double credit = paymentClient.availableCredit("Rick", "111111111111");
        assertEquals(500, credit, 0.1);
        paymentClient.shutDownChannel();

        UfosParkClient ufosClient = UfosParkClient.init();
        String ufoID = ufosClient.UfoOf("Rick", "111111111111");
        assertNotEquals("no UFO", ufoID);
        ufosClient.shutDownChannel();
    }
}