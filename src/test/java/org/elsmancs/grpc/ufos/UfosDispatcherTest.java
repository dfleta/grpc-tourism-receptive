package org.elsmancs.grpc.ufos;

import static org.junit.Assert.assertEquals;

import org.elsmancs.grpc.payment.PaymentClient;
import org.elsmancs.grpc.payment.PaymentServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UfosDispatcherTest {

    private UfosParkServer serverUfo = null;
    private PaymentServer serverPayment = null;

    @Before
    public void setup() throws Exception {

        serverUfo = new UfosParkServer();
        serverUfo.start();

        serverPayment = new PaymentServer();
        serverPayment.start();
    }

    @After
    public void tearDown() throws Exception {
        serverUfo.stop();
        serverPayment.stop();
    }

    @Test
    public void dispatch_test() throws Exception {

        UfosDispatcher dispatcher = new UfosDispatcher();
        dispatcher.dispatch("Rick", "111111111111");

        PaymentClient paymentClient = PaymentClient.init();
        double credit = paymentClient.availableCredit("Rick", "111111111111");
        assertEquals(500, credit, 0.1);        
    }

}