/**
 * Testing the client of the
 * crystal dispenser service. 
 */

package org.elsmancs.grpc.crystal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Crystal;
import org.elsmancs.grpc.Processed;
import org.elsmancs.grpc.CrystalExpenderGrpc.CrystalExpenderImplBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.util.MutableHandlerRegistry;

@RunWith(JUnit4.class)
public class CrystalClientTest {
    /**
     * Test classes uses a specific grpc Junit rule for testing
     * clients and servers. See grpc documentation.
     * 
     * grpc documentation comments:
     * This rule manages automatic graceful shutdown for the registered server at
     * the end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();

    private CrystalClient client;

    @Before
    public void setUp() throws Exception {

        // grpc documentation comments:
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // grpc documentation comments:
        // Use a mutable service registry for later registering the service
        // implementation
        // for each test case.
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).fallbackHandlerRegistry(serviceRegistry)
                .directExecutor().build().start());

        client = new CrystalClient(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    }

    /**
     * dispatch service testing
     */
    @Test
    public void dispatch_test() {

        // request CreditCard message
        CreditCard requestCard = CreditCard.newBuilder().setOwner("Rick").setNumber("1111").build();
        final AtomicReference<CreditCard> cardDelivered = new AtomicReference<CreditCard>();

        // Mocking the service reply /message Crystal
        Crystal responseCrystal = Crystal.newBuilder().setUnidades(1).setFee(50).build();

        // Implementing the fake service
        CrystalExpenderImplBase dispatchImpl = new CrystalExpenderImplBase() {
            @Override
            public void dispatch(CreditCard card, StreamObserver<Crystal> responseObserver) {
                // Checking the CreditCard message construction at the client
                cardDelivered.set(card);
                responseObserver.onNext(responseCrystal);
                responseObserver.onCompleted();
            }
        };

        serviceRegistry.addService(dispatchImpl);

        Crystal crystalDelivered = client.Dispatch("Rick", "1111");

        assertEquals(requestCard, cardDelivered.get());
        assertEquals(responseCrystal.getUnidades(), crystalDelivered.getUnidades(), 0);
        assertEquals(responseCrystal.getFee(), crystalDelivered.getFee(), 0.1);
    }

    /**
     * dispatch service returns an error
     */
    @Test
    public void dispatch_error_test() {

        // request message CreditCard
        CreditCard requestCard = CreditCard.newBuilder().setOwner("Rick").setNumber("1111").build();
        final AtomicReference<CreditCard> cardDelivered = new AtomicReference<CreditCard>();

        // If the server responses an error, the client returns false
        final StatusRuntimeException fakeError = new StatusRuntimeException(io.grpc.Status.DATA_LOSS);

        // Implementing the fake service
        CrystalExpenderImplBase dispatchImpl = new CrystalExpenderImplBase() {
            @Override
            public void dispatch(CreditCard card, StreamObserver<Crystal> responseObserver) {
                // Checking the CreditCard message construction at the client
                cardDelivered.set(card);
                responseObserver.onError(fakeError);
            }
        };

        serviceRegistry.addService(dispatchImpl);

        Crystal crystalDelivered = client.Dispatch("Rick", "1111");

        assertEquals(requestCard, cardDelivered.get());
        assertNull(crystalDelivered);
    }

    /**
     * confirm service testing
     */
    @Test
    public void confirm_test() {

        // request message Crystal
        Crystal request = Crystal.newBuilder().setUnidades(1).build();
        AtomicReference<Crystal> crystalDelivered = new AtomicReference<Crystal>();

        // Mocking the service reply /message Processed
        Processed responseProcessed = Processed.newBuilder().setIsProcessed(true).build();

        // Fake service
        CrystalExpenderImplBase confirmImpl = new CrystalExpenderImplBase() {
            @Override
            public void confirm(Crystal request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {
                // UFO message construction
                crystalDelivered.set(request);
                responseObserver.onNext(responseProcessed);
                responseObserver.onCompleted();
            }
        };

        serviceRegistry.addService(confirmImpl);

        boolean processedDelivered = client.Confirm(1);

        assertEquals(request, crystalDelivered.get());
        assertEquals(responseProcessed.getIsProcessed(), processedDelivered);
    }

    /**
     * confirm service returns an error
     */
    @Test
    public void confirm_error_test() {

        // request message Crystal
        Crystal requestCrytal = Crystal.newBuilder().setUnidades(1).build();
        AtomicReference<Crystal> crystalDelivered = new AtomicReference<Crystal>();

        // If the server responses an error, the client returns false
        final StatusRuntimeException fakeError = new StatusRuntimeException(io.grpc.Status.DATA_LOSS);

        // Fake service
        CrystalExpenderImplBase confirmImpl = new CrystalExpenderImplBase() {
            @Override
            public void confirm(Crystal request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {
                crystalDelivered.set(request);
                responseObserver.onError(fakeError);;
            }
        };

        serviceRegistry.addService(confirmImpl);

        boolean processedDelivered = client.Confirm(1);

        assertEquals(requestCrytal, crystalDelivered.get());
        assertFalse(processedDelivered);
    }
}