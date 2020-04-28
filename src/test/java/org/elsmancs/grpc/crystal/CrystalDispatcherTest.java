package org.elsmancs.grpc.crystal;


import static org.junit.Assert.assertEquals;

import org.elsmancs.grpc.payment.PaymentClient;
import org.elsmancs.grpc.payment.PaymentServer;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;

public class CrystalDispatcherTest {

    private CrystalServer serverCrystal = null;
    private PaymentServer serverPayment = null;

    @Before
    public void setup() throws Exception {

        serverCrystal = new CrystalServer();
        serverCrystal.start();

        serverPayment = new PaymentServer();
        serverPayment.start();
    }

    @After
    public void tearDown() throws Exception {
        serverCrystal.stop();
        serverPayment.stop();
    }

    @Test
    public void dispatch_test() throws Exception {

        CrystalDispatcher dispatcher = new CrystalDispatcher();
        dispatcher.dispatch("Rick", "111111111111");

        PaymentClient paymentClient = PaymentClient.init();
        double credit = paymentClient.availableCredit("Rick", "111111111111");
        assertEquals(2950, credit, 0.1);
        paymentClient.shutDownChannel();
    }
}