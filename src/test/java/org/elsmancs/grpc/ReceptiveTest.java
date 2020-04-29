package org.elsmancs.grpc;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.elsmancs.grpc.crystal.CrystalDispatcher;
import org.elsmancs.grpc.crystal.CrystalServer;
import org.elsmancs.grpc.payment.PaymentClient;
import org.elsmancs.grpc.payment.PaymentServer;
import org.elsmancs.grpc.ufos.UfosDispatcher;
import org.elsmancs.grpc.ufos.UfosParkServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReceptiveTest {

    private UfosParkServer serverUfos = null;
    private PaymentServer serverPayment = null;
    private CrystalServer serverCrystal = null;

    private Receptive receptive = null;

    @Before
    public void setup() throws Exception {
        
        serverUfos = new UfosParkServer();
        serverUfos.start();

        serverPayment = new PaymentServer();
        serverPayment.start();

        serverCrystal = new CrystalServer();
        serverCrystal.start();
    }

    @After
    public void tearDown() throws Exception {
        serverUfos.stop();
        serverPayment.stop();
        serverCrystal.stop();
    }

    @Test
    public void register_test() {

        receptive = new Receptive();

        receptive.register(new UfosDispatcher());
        receptive.register(new CrystalDispatcher());
        
        String observers = List.of(CrystalDispatcher.class.getSimpleName(),
                                    UfosDispatcher.class.getSimpleName()).toString();

        assertEquals(observers, receptive.toString());   
    }

    @Test
    public void dispatch_test() throws Exception {

        receptive = new Receptive();

        receptive.register(new UfosDispatcher());
        receptive.register(new CrystalDispatcher());

        receptive.dispatch("Rick", "111111111111");

        PaymentClient paymentClient = PaymentClient.init();
        double credit = paymentClient.availableCredit("Rick", "111111111111");
        assertEquals(450, credit, 0.1);
        paymentClient.shutDownChannel();
    }
}