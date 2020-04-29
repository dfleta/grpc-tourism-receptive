
/**
 * Server side Streaming RPC
 */

package org.elsmancs.grpc.crystal;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Crystal;
import org.elsmancs.grpc.CrystalExpenderGrpc;
import org.elsmancs.grpc.Processed;

/**
 * Server that manages startup/shutdown of a {@code UfosPark} server.
 */
public class CrystalServer {

    private static final Logger logger = Logger.getLogger(CrystalServer.class.getName());

    private Server server;
    private int port;

    public CrystalServer() {
        this.port = 50071;
        this.server = ServerBuilder.forPort(port).addService(new CrystalService()).build();
    }

    /** 
     * These constructor has been added for testing purposes.
     * Create a Crytal server using serverBuilder as a base.
     */
    public CrystalServer(ServerBuilder<?> serverBuilder, int port) {
        this.server = serverBuilder.addService(new CrystalService()).build();
        this.port = port;

    }

    public void start() throws IOException {

        this.server.start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown
                // hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    CrystalServer.this.stop();
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
        final CrystalServer server = new CrystalServer();
        server.start();
        server.blockUntilShutdown();
    }

    /**
     * CrystalDispenser service implementation. 
     * See crystal.proto for details.
     */
    public static class CrystalService extends CrystalExpenderGrpc.CrystalExpenderImplBase {

        private final CrystalDispenser crystalExpender = new CrystalDispenser();

        @Override
        public void dispatch(CreditCard request, StreamObserver<org.elsmancs.grpc.Crystal> responseObserver) {

            int units = crystalExpender.dispatch(request.getNumber());
            
            Crystal reply = Crystal.newBuilder().setUnidades(units).setFee(crystalExpender.fee()).build();
            // return the Crystal message
            responseObserver.onNext(reply);
            // Specify that we’ve finished dealing with the RPC.
            responseObserver.onCompleted();
        }

        @Override
        public void confirm(Crystal request, StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {

            boolean isConfirmed = crystalExpender.confirm(request.getUnidades());

            Processed reply = Processed.newBuilder().setIsProcessed(isConfirmed).build();
            // return the Processed message
            responseObserver.onNext(reply);
            // Specify that we’ve finished dealing with the RPC.
            responseObserver.onCompleted();
        }
    }
}
