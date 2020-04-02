package org.elsmancs.grpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

@RunWith(JUnit4.class)
public class PaymentClientTest {

    /**
     * This rule manages automatic graceful shutdown for the registered servers and
     * channels at the end of test.
     * 
     * https://junit.org/junit4/javadoc/4.12/org/junit/rules/TestRule.html
     * 
     * @Rule anota reglas a nivel de método A rule is an alteration in how a test
     *       method, or set of test methods, is run and reported. Similar a before.
     *       etc pero más flexible.
     * 
     *       Esta regla crea un grpcCleanup antes de la ejecución de cada test. *
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /**
     * Mock del servicio: la implementación base es la clase PaymentImplBase, de la
     * clase PaymentGrpc, que dispone del metodo pay() del servicio.
     * A estas alturas del desarrollo, aun no disponemos del servidor PaymentServer,
     * por lo que la implementacion de PaymentImplBase aun no existe
     * y hay que mockear su existencia (ver delegatesTo())
     * PaymentGrpc existe pues es generado mediante grpc-protocolbuffers y protoc
     */
    private final PaymentGrpc.PaymentImplBase serviceImpl = mock(PaymentGrpc.PaymentImplBase.class, delegatesTo(
        
            // La implementacion del servico pay en PaymentImplBase aun no existe
            // y es necesario implementarla aqui, en el delegatesTo.
            // Delegamos la pregunta al metodo pay de la clase PaymentImplBase a
            // esta implementacion pay PaymentImplBase incluida en el delegatesTo()
            new PaymentGrpc.PaymentImplBase() {
                // Si ejecutamos los casos test, pasan, pero el servidor PaymentServer no existe,
                // por lo que el método Pay, en la línea logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                // recive:
                // By default the client will receive Status.UNIMPLEMENTED for all RPCs.
                // e informa:
                // ADVERTENCIA: RPC failed: Status{code=UNIMPLEMENTED, description=Method payment.Payment/Pay is unimplemented, cause=null}
                // 
                // You might need to implement necessary behaviors for your test here, like
                // this:
                //
                // @Override
                // public void sayHello(HelloRequest request, StreamObserver<HelloReply>
                // respObserver) {
                // respObserver.onNext(HelloReply.getDefaultInstance());
                // respObserver.onCompleted();
                // }
                public void pay(CreditCard request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {
                    Processed reply = Processed.newBuilder().setIsProcessed(true).build();
                    responseObserver.onNext(reply);
                    responseObserver.onCompleted();
                }   
            }));

    private PaymentClient client;

    @Before
    public void setUp() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful
        // shutdown.
        grpcCleanup.register(

                // aqui registro el servicio serviceImpl PaymentBaseImpl.pay() 
                // que he moqueado en delegatesTo()
                InProcessServerBuilder.forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup
                .register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

        // Create a PaymentClient using the in-process channel;
        //
        // De este modo, en el cliente: 
        // blockingStub = PaymentGrpc.newBlockingStub(channel);
        // y cuando invocamos el metodo pay()
        // se ejecuta
        // response = blockingStub.pay(request) => que implica 
        // una llamada al metodo pay() del service en PaymentImplBase
        // metodo que hemos moqueado en delegatesTo()
        client = new PaymentClient(channel);
    }

    /**
     * To test the client, call from the client against the fake server, and verify
     * behaviors or state changes from the server side.
     */
    @Test
    public void creditCard_messageDeliveredToServer() {

        // https://www.javadoc.io/doc/org.mockito/mockito-core/2.2.22/org/mockito/Mockito.html#15
        // ArgumentCaption => helpful to assert on certain arguments after the actual
        // verification.
        // En requestCaptor dispondremos de los argumentos del mensaje CreditCard
        // que se construye dentro del metodo Pay() del cliente,
        // para assert sobre ellos y chequear que contienen los valores 
        // que le hemos pasado al servicio.

        ArgumentCaptor<CreditCard> requestCaptor = ArgumentCaptor.forClass(CreditCard.class);

        boolean isAuthorised = client.Pay("Rick", "1111", 500); 

        // En client.Pay() se produce la llamada al mock => delegado al pay en delegatesTo()
        // Pay() metodo del cliente
        // donde se ejecuta
        // response = blockingStub.pay(request) => que implica 
        // una llamada al metodo pay() del service
        // metodo que hemos moqueado en delegatesTo()
        // Si comentamos esta línea recibimos:
        //  Wanted but not invoked:
        //      paymentImplBase.pay(
        //      <Capturing argument>,
        //      <any>

        // Capturar los argumentos de CreditCard exige usarlo junto a verify.
        // Verificamos el comportamiento del servicio mockeado servicioImpl.
        // pay espera:
        // Un mensaje CreditCard como primer arg = requestCaptor:  ArgumentCaptor 
        //      may be a better fit if we need it to assert on argument values
        //      to complete verification
        // Un mensaje Processed como segundo arg => Custom argument matchers 
        //      via ArgumentMatcher are usually better for stubbing.
        //  any() es el argumentMatcher: le pasamos cualquier cosa del tipo Processed
        //  Con los genericos parametrizamos la clase any() al tipo especifico
        //  que requiere pay(): un Processed que luego será la respuesta del metodo:
        // argumentsMatcher no permite assert, sólo stubbing, así que la respuesta
        // Processed la obtengo en:
        // boolean isAuthorised = client.Pay("Rick", "1111", 500); 
        // para chequearla al final del test.
        // https://www.baeldung.com/mockito-argument-matchers

        verify(serviceImpl) 
                .pay(requestCaptor.capture(), ArgumentMatchers.<StreamObserver<Processed>>any());

        // Chequeamos que el PaymentClient.Pay() ha creado el mensaje
        // CreditCard para el servicio.
        // La responsabilidad del cliente es construir ese mensaje
        // y devolver la respuesta Processed
        assertEquals("Rick", requestCaptor.getValue().getOwner());
        assertEquals("1111", requestCaptor.getValue().getNumber());
        assertEquals(500, requestCaptor.getValue().getCharge(), 0.1);
        assertTrue(isAuthorised);
    }
}
