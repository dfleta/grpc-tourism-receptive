/**
 * Server Ufos Park logic testing.
 * Integration with service Ufos Park 
 * business logic and data repo.
 */

package org.elsmancs.grpc.ufos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Processed;
import org.elsmancs.grpc.UfosParkGrpc;
import org.elsmancs.grpc.Ufo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;

@RunWith(JUnit4.class)
public class UfosParkServerTest {

    /**
     * From grpc documentation examples:
     * "This rule manages automatic graceful shutdown for the registered servers and
     * channels at the end of test.
     */
    @Rule
    public final GrpcCleanupRule  grpcCleanup = new GrpcCleanupRule();

    /**
     * The class "GrpcServerRule" (from "grpc-java/testing") 
     * is a JUnit TestRule that creates a InProcessServer and a ManagedChannel.
     */

    public UfosParkServer server = null;
    public ManagedChannel inProcessChannel = null; 

    @Before
    public void setup() throws Exception {

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful
        // shutdown.
        int port = 50061;
        server = new UfosParkServer(InProcessServerBuilder.forName(serverName).directExecutor(),
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
     * Devuelve un UFO pues hay disponibles
     */
    @Test
    public void ufosParkService_dispatch_responseMessage_UFOavailable() {

        UfosParkGrpc.UfosParkBlockingStub blockingStub = UfosParkGrpc.newBlockingStub(inProcessChannel);

        CreditCard request = CreditCard.newBuilder()
                                        .setOwner("Rick")
                                        .setNumber("111111111111")
                                        .build();

        org.elsmancs.grpc.Ufo response = blockingStub.dispatch(request);

        assertEquals("111111111111", response.getCardNumber());
        assertNotEquals("no ufo reserved", response.getId());        
    }

    /**
     * Asigna el UFO indicado a la tarjeta indicada
     * siempre que la ID del UFo exista en la BBDD
     */
    @Test
    public void ufosParkService_assignUfo_responseMessage_isProcessed() {

        UfosParkGrpc.UfosParkBlockingStub blockingStub = UfosParkGrpc.newBlockingStub(inProcessChannel);

        Ufo request = Ufo.newBuilder().setId("unx").setCardNumber("111111111111").setFee(500).build();

        Processed processed = blockingStub.assignUfo(request);

        assertTrue(processed.getIsProcessed());
    }

    /**
     * No asigna el UFO indicado a la tarjeta indicada
     * porque la ID del UFO no existe en la BBDD
     */
    @Test
    public void ufosParkService_assignUfo_responseMessage_NOTprocessed() {

        UfosParkGrpc.UfosParkBlockingStub blockingStub = UfosParkGrpc.newBlockingStub(inProcessChannel);

        Ufo request = Ufo.newBuilder().setId("cienex").setCardNumber("111111111111").setFee(500).build();

        Processed processed = blockingStub.assignUfo(request);

        assertFalse(processed.getIsProcessed());
    }

    /**
     * Tras reservar todos los UFOs disponibles,
     * intentarmos reservar otro.
     */
    @Test
    public void ufosParkService_dispatch_responseMessage_NoUFOavailable() {

        UfosParkGrpc.UfosParkBlockingStub blockingStub = UfosParkGrpc.newBlockingStub(inProcessChannel);

        // Ufo para Rick
        CreditCard request = CreditCard.newBuilder()
                                        .setOwner("Rick")
                                        .setNumber("111111111111")
                                        .build();

        Ufo response = blockingStub.dispatch(request);
        assertEquals("111111111111", response.getCardNumber());
        assertNotEquals("no ufo reserved", response.getId());
        
        Processed processed = blockingStub.assignUfo(response);
        assertTrue(processed.getIsProcessed());

        // Ufo para Abradolph
        request = CreditCard.newBuilder()
                                .setOwner("Abradolph")
                                .setNumber("222222222222")
                                .build();
        response = blockingStub.dispatch(request);
        assertEquals("222222222222", response.getCardNumber());
        assertNotEquals("no ufo reserved", response.getId());
        
        processed = blockingStub.assignUfo(response);
        assertTrue(processed.getIsProcessed());

        // Ufo para GearHead
        request = CreditCard.newBuilder()
                                .setOwner("Gear Head")
                                .setNumber("333333333333")
                                .build();
        response = blockingStub.dispatch(request);
        assertEquals("333333333333", response.getCardNumber());
        assertNotEquals("no ufo reserved", response.getId());

        processed = blockingStub.assignUfo(response);
        assertTrue(processed.getIsProcessed());

        // No hay Ufo para Morty
        request = CreditCard.newBuilder()
                                .setOwner("Morty")
                                .setNumber("444444444444")
                                .build();
        response = blockingStub.dispatch(request);
        assertEquals("444444444444", response.getCardNumber());
        assertEquals("no ufo reserved", response.getId());
    }

}