
/**
 * Server side Streaming RPC
 */

package org.elsmancs.grpc.ufos;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Processed;
import org.elsmancs.grpc.Ufo;
import org.elsmancs.grpc.UfosParkGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code UfosPark} server.
 */
public class UfosParkServer {

    private static final Logger logger = Logger.getLogger(UfosParkServer.class.getName());

    private Server server;
    private int port;

    public UfosParkServer() {
        // The port on which the server should run
        this.port = 50051;
        server = ServerBuilder.forPort(port)
                                .addService(new UfosParkService())
                                .build();
    }

    /** 
     * These constructor has been added for testing purposes.
     * Create a UfosPark server using serverBuilder as a base.
     */
    public UfosParkServer(ServerBuilder<?> serverBuilder, int port) {
        this.server =  serverBuilder.addService(new UfosParkService()).build();
        this.port = port;
    }

    public void start() throws IOException {

        server.start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown
                // hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    UfosParkServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final UfosParkServer server = new UfosParkServer();
        server.start();
        server.blockUntilShutdown();
    }

    /**
     * UfosPark service implementation. 
     * See ufos_park.proto for details.
     */
    static class UfosParkService extends UfosParkGrpc.UfosParkImplBase {

        private UfosPark ufosPark = new UfosPark();

        @Override
        public void dispatch(CreditCard request, StreamObserver<org.elsmancs.grpc.Ufo> responseObserver) {

            String ufoID = ufosPark.reserveUfo(request.getNumber());
            // Method chaining
            Ufo reply = Ufo.newBuilder().setId(ufoID).setCardNumber(request.getNumber()).setFee(ufosPark.fee()).build();
            // Return the UFO message
            responseObserver.onNext(reply);
            // Specify that we’ve finished dealing with the RPC.
            responseObserver.onCompleted();
        }

        @Override
        public void assignUfo(Ufo request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {

            boolean isAssigned = ufosPark.assignUfo(request.getId(), request.getCardNumber());

            Processed reply = Processed.newBuilder().setIsProcessed(isAssigned).build();
            // return the Processed message
            responseObserver.onNext(reply);
            // Specify that we’ve finished dealing with the RPC.
            responseObserver.onCompleted();
        }

        @Override
        public void ufoOf(CreditCard request, StreamObserver<org.elsmancs.grpc.Ufo> responseObserver) {
            
            String ufoID = ufosPark.getUfoOf(request.getNumber());

            Ufo reply = Ufo.newBuilder().setId(ufoID).setCardNumber(request.getNumber()).build();
            // return the Ufo message
            responseObserver.onNext(reply);
            // Specify that we’ve finished dealing with the RPC.
            responseObserver.onCompleted();
        }
    }
}
