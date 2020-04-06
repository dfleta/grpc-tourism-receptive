package org.elsmancs.grpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;

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
     * This rule manages automatic graceful shutdown for the registered server at
     * the end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();

    private CrystalClient client;

    @Before
    public void setUp() throws Exception {

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Use a mutable service registry for later registering the service
        // implementation
        // for each test case.
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).fallbackHandlerRegistry(serviceRegistry)
                .directExecutor().build().start());

        client = new CrystalClient(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    }

    /**
     * Test del servicio dispatch
     */
    @Test
    public void dispatch_test() {

        // Mensaje CreditCard al servicio
        CreditCard requestCard = CreditCard.newBuilder().setOwner("Rick").setNumber("1111").build();
        final AtomicReference<CreditCard> cardDelivered = new AtomicReference<CreditCard>();

        // Mock de la respuesta /mensaje Crystal del servicio
        Crystal responseCrystal = Crystal.newBuilder().setUnidades(1).setFee(50).build();

        // implement the fake service
        CrystalExpenderImplBase dispatchImpl = new CrystalExpenderImplBase() {
            @Override
            public void dispatch(CreditCard card, StreamObserver<Crystal> responseObserver) {
                // para chequear que la construccion de la Card en el client se realiza OK
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

    @Test
    public void dispatch_error_test() {

        // Mensaje CreditCard al servicio
        CreditCard requestCard = CreditCard.newBuilder().setOwner("Rick").setNumber("1111").build();
        final AtomicReference<CreditCard> cardDelivered = new AtomicReference<CreditCard>();

        // Si el servidor responde con un error, el cliente devuelve false
        final StatusRuntimeException fakeError = new StatusRuntimeException(io.grpc.Status.DATA_LOSS);

        // implement the fake service
        CrystalExpenderImplBase dispatchImpl = new CrystalExpenderImplBase() {
            @Override
            public void dispatch(CreditCard card, StreamObserver<Crystal> responseObserver) {
                // para chequear que la construccion de la Card en el client se realiza OK
                cardDelivered.set(card);
                responseObserver.onError(fakeError);
            }
        };

        serviceRegistry.addService(dispatchImpl);

        Crystal crystalDelivered = client.Dispatch("Rick", "1111");

        assertEquals(requestCard, cardDelivered.get());
        assertNull(crystalDelivered);
    }

    @Test
    public void confirm_test() {

        // Mensaje Crystal al servicio
        Crystal request = Crystal.newBuilder().setUnidades(1).build();
        AtomicReference<Crystal> crystalDelivered = new AtomicReference<Crystal>();

        // Mock de la respuesta /mensaje Processed del servicio
        Processed responseProcessed = Processed.newBuilder().setIsProcessed(true).build();

        // fake service
        CrystalExpenderImplBase confirmImpl = new CrystalExpenderImplBase() {
            @Override
            public void confirm(Crystal request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {
                // para chequear que la construccion del Ufo en el client se realiza OK
                crystalDelivered.set(request);
                // return the Ufo
                responseObserver.onNext(responseProcessed);
                // Specify that we’ve finished dealing with the RPC.
                responseObserver.onCompleted();
            }
        };

        serviceRegistry.addService(confirmImpl);

        boolean processedDelivered = client.Confirm(1);

        assertEquals(request, crystalDelivered.get());
        assertEquals(responseProcessed.getIsProcessed(), processedDelivered);
    }

    @Test
    public void confirm_error_test() {

        // Mensaje Crystal al servicio
        Crystal requestCrytal = Crystal.newBuilder().setUnidades(1).build();
        AtomicReference<Crystal> crystalDelivered = new AtomicReference<Crystal>();

        // Si el servidor responde con un error, el cliente devuelve false
        final StatusRuntimeException fakeError = new StatusRuntimeException(io.grpc.Status.DATA_LOSS);

        // fake service
        CrystalExpenderImplBase confirmImpl = new CrystalExpenderImplBase() {
            @Override
            public void confirm(Crystal request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {
                // para chequear que la construccion del Ufo en el client se realiza OK
                crystalDelivered.set(request);
                // return the Ufo
                responseObserver.onError(fakeError);;
            }
        };

        serviceRegistry.addService(confirmImpl);

        boolean processedDelivered = client.Confirm(1);

        assertEquals(requestCrytal, crystalDelivered.get());
        assertFalse(processedDelivered);
    }
}