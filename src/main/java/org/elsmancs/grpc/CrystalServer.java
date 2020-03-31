
/**
 * Server side Streaming RPC
 */

package org.elsmancs.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code UfosPark} server.
 */
public class CrystalServer {

  private static final Logger logger = Logger.getLogger(CrystalServer.class.getName());

  private Server server;

  private void start() throws IOException {

    /* The port on which the server should run */
    int port = 50071;
    server = ServerBuilder.forPort(port)
                          .addService(new CrystalService())
                          .build()
                          .start();

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

  private void stop() throws InterruptedException {
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
   * Implementacion del servicio UfosPark. Ver fichero ufos_park.proto para
   * detalles.
   */
  static class CrystalService extends CrystalExpenderGrpc.CrystalExpenderImplBase {
    
    private CrystalExpender crystalExpender = new CrystalExpender(); 

    @Override
    public void dispatch(CreditCard request, 
                         StreamObserver<org.elsmancs.grpc.Crystal> responseObserver) {

      int unidades = crystalExpender.dispatch(request);
      // Como construir un mensaje con varias propiedades:
      // method chaining
      Crystal reply = Crystal.newBuilder()
                              .setUnidades(unidades)
                              .setFee(crystalExpender.fee())
                              .build();
      // return Collaxion units
      responseObserver.onNext(reply);
      // Specify that we’ve finished dealing with the RPC.
      responseObserver.onCompleted();
    }

    @Override
    public void confirm(Crystal request, 
                         StreamObserver<org.elsmancs.grpc.Processed> responseObserver) {

        boolean isConfirmed = crystalExpender.confirm(request);
        // Como construir un mensaje con varias propiedades:
        // method chaining
        Processed reply = Processed.newBuilder()
                                    .setIsProcessed(isConfirmed)
                                    .build();
        // return the Ufo
        responseObserver.onNext(reply);
        // Specify that we’ve finished dealing with the RPC.
        responseObserver.onCompleted();
    }
  }
}
