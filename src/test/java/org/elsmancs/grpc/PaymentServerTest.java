
package org.elsmancs.grpc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test de unidad para {@link PaymentServer}
 */

@RunWith(JUnit4.class)
public class PaymentServerTest {
    /**
     * From grpc documentation examples:
     * "This rule manages automatic graceful shutdown for the registered servers and
     * channels at the end of test.""
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private PaymentServer server;
    private ManagedChannel inProcessChannel;

    @Before
    public void setUp() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Use directExecutor for both InProcessServerBuilder and
        // InProcessChannelBuilder can reduce the
        // usage timeouts and latches in test. But we still add timeout and latches
        // where they would be
        // needed if no directExecutor were used, just for demo purpose.

        // Create a server, add service, start, and register for automatic graceful
        // shutdown.
        int port = 50061;
        server = new PaymentServer(InProcessServerBuilder.forName(serverName).directExecutor(),
                                    port);

        server.start();

        // Create a client channel and register for automatic graceful shutdown.
        inProcessChannel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    /**
     * To test the server, make calls with a real stub using the in-process channel,
     * and verify behaviors or state changes from the client side.
     */

    /**
     * Procesa el cargo pues existe credito suficiente
     */
    @Test
    public void paymentService_replyMessage_isProcessed() throws Exception {

        PaymentGrpc.PaymentBlockingStub blockingStub = PaymentGrpc.newBlockingStub(inProcessChannel);


        Processed reply = blockingStub
                .pay(CreditCard.newBuilder().setOwner("Rick").setNumber("1111").setCharge(3000).build());

        assertTrue(reply.getIsProcessed());
    }

    /**
     * No procesa el cargo pues no existe credito suficiente
     */
    @Test
    public void paymentService_replyMessage_isNotProcessed() throws Exception {

        PaymentGrpc.PaymentBlockingStub blockingStub = PaymentGrpc.newBlockingStub(inProcessChannel);

        Processed reply = blockingStub.pay(CreditCard.newBuilder().setOwner("Rick").setNumber("1111").setCharge(4000).build());

        assertFalse(reply.getIsProcessed());
    }

    /**
     * Procesa sucesivos cargos a la misma tarjeta
     */
    @Test
    public void paymentService_replyMessage_several_charges_processed() {
        
        PaymentGrpc.PaymentBlockingStub blockingStub = PaymentGrpc.newBlockingStub(inProcessChannel);

        Processed reply = null;

        CreditCard card = CreditCard.newBuilder().setOwner("Rick").setNumber("1111").setCharge(1000).build();

        for (int i=0; i<=2; i++) {
            reply = blockingStub.pay(card);
            assertTrue(reply.getIsProcessed());
        }
        
        reply = blockingStub.pay(card);
        assertFalse(reply.getIsProcessed());        
    }
}
