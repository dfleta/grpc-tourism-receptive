/**
 * Testing the client of the
 * Ufos Park service. 
 */

package org.elsmancs.grpc.ufos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Processed;
import org.elsmancs.grpc.Ufo;
import org.elsmancs.grpc.UfosParkGrpc.UfosParkImplBase;
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
public class UfosParkClientTest {
    /**
     * This rule manages automatic graceful shutdown for the registered server at
     * the end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();

    private UfosParkClient client;

    @Before
    public void setUp() throws Exception {

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Use a mutable service registry for later registering the service
        // implementation
        // for each test case.
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).fallbackHandlerRegistry(serviceRegistry)
                .directExecutor().build().start());

        client = new UfosParkClient(
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

        // Mock de la respuesta /mensaje UFO del servicio
        Ufo responseUfo = Ufo.newBuilder().setCardNumber("1111").setFee(500).build();

        // implement the fake service
        UfosParkImplBase dispatchImpl = new UfosParkImplBase() {
            @Override
            public void dispatch(CreditCard card, StreamObserver<Ufo> responseObserver) {
                // para chequear que la construccion de la Card en el client se realiza OK
                cardDelivered.set(card);
                responseObserver.onNext(responseUfo);
                responseObserver.onCompleted();
            }
        };

        serviceRegistry.addService(dispatchImpl);

        Ufo ufoDelivered = client.Dispatch("Rick", "1111");

        assertEquals(requestCard, cardDelivered.get());
        assertEquals(responseUfo.getCardNumber(), ufoDelivered.getCardNumber());
        assertEquals(responseUfo.getFee(), ufoDelivered.getFee(), 0.1);
    }

    @Test
    public void dispatch_error_test() {

        // Mensaje CreditCard al servicio
        CreditCard requestCard = CreditCard.newBuilder().setOwner("Rick").setNumber("1111").build();
        final AtomicReference<CreditCard> cardDelivered = new AtomicReference<CreditCard>();

        // Si el servidor responde con un error, el cliente devuelve false
        final StatusRuntimeException fakeError = new StatusRuntimeException(io.grpc.Status.DATA_LOSS);

        // implement the fake service
        UfosParkImplBase dispatchImpl = new UfosParkImplBase() {
            @Override
            public void dispatch(CreditCard card, StreamObserver<Ufo> responseObserver) {
                // para chequear que la construccion de la Card en el client se realiza OK
                cardDelivered.set(card);
                responseObserver.onError(fakeError);
            }
        };

        serviceRegistry.addService(dispatchImpl);

        Ufo ufoDelivered = client.Dispatch("Rick", "1111");

        assertEquals(requestCard, cardDelivered.get());
        assertNull(ufoDelivered);
    }

    @Test
    public void assignUfo_test() {

        // Mensaje Ufo al servicio
        Ufo requestUfo = Ufo.newBuilder().setId("unox").setCardNumber("1111").build();
        AtomicReference<Ufo> ufoDelivered = new AtomicReference<Ufo>();

        // Mock de la respuesta /mensaje Processed del servicio
        Processed responseProcessed = Processed.newBuilder().setIsProcessed(true).build();

        // fake service
        UfosParkImplBase assignImpl = new UfosParkImplBase() {
            @Override
            public void assignUfo(Ufo request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {
                // para chequear que la construccion del Ufo en el client se realiza OK
                ufoDelivered.set(request);
                // return the Ufo
                responseObserver.onNext(responseProcessed);
                // Specify that we’ve finished dealing with the RPC.
                responseObserver.onCompleted();
            }
        };

        serviceRegistry.addService(assignImpl);

        boolean processedDelivered = client.AssignUfo("unox", "1111");

        assertEquals(requestUfo, ufoDelivered.get());
        assertEquals(responseProcessed.getIsProcessed(), processedDelivered);
    }

    @Test
    public void assignUfo_error_test() {

        // Mensaje Ufo al servicio
        Ufo requestUfo = Ufo.newBuilder().setId("unox").setCardNumber("1111").build();
        AtomicReference<Ufo> ufoDelivered = new AtomicReference<Ufo>();

        // Si el servidor responde con un error, el cliente devuelve false
        final StatusRuntimeException fakeError = new StatusRuntimeException(io.grpc.Status.DATA_LOSS);

        // fake service
        UfosParkImplBase assignImpl = new UfosParkImplBase() {
            @Override
            public void assignUfo(Ufo request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {
                // para chequear que la construccion del Ufo en el client se realiza OK
                ufoDelivered.set(request);
                // return the Ufo
                responseObserver.onError(fakeError);
            }
        };

        serviceRegistry.addService(assignImpl);

        boolean processedDelivered = client.AssignUfo("unox", "1111");

        assertEquals(requestUfo, ufoDelivered.get());
        assertFalse(processedDelivered);
    }

    @Test
    public void UfoOf_test() {

        // Mensaje CreditCard al servicio
        CreditCard card = CreditCard.newBuilder()
                                        .setOwner("Rick")
                                        .setNumber("111111111111")
                                        .build();

        AtomicReference<CreditCard> cardDelivered = new AtomicReference<CreditCard>();

        // Mock de la respuesta /mensaje Processed del servicio
        Ufo responseUfo = Ufo.newBuilder().setId("unox").setCardNumber("111111111111").setFee(500).build();

        // Fake service
        UfosParkImplBase assignImpl = new UfosParkImplBase() {
            @Override
            public void ufoOf(CreditCard request, StreamObserver<org.elsmancs.grpc.Ufo> response) {
                // para chequear que la construccion del Ufo en el client se realiza OK
                cardDelivered.set(request);
                // return the Ufo
                response.onNext(responseUfo);
                // Specify that we’ve finished dealing with the RPC.
                response.onCompleted();
            }
        };

        serviceRegistry.addService(assignImpl);

        String ufoID = client.UfoOf("Rick", "111111111111");

        assertEquals(card, cardDelivered.get());
        assertEquals(responseUfo.getId(), ufoID);
    }    
}