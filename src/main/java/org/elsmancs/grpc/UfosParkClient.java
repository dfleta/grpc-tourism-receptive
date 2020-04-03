
/**
 * Client side Streaming RPC
 */

package org.elsmancs.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a UFO from the {@link UfosParkServer}.
 */
public class UfosParkClient {

    private static final Logger logger = Logger.getLogger(UfosParkClient.class.getName());

    private final UfosParkGrpc.UfosParkBlockingStub blockingStub;

    // OCP
    private ManagedChannel channel = null;


    /**
     * Construct client for accessing UfosParkServer using the existing channel.
     */
    public UfosParkClient(Channel channel) {

        // 'channel' here is a Channel, not a ManagedChannel,
        // so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test
        // and makes it easier to reuse Channels.
        blockingStub = UfosParkGrpc.newBlockingStub(channel);
    }

    // Obtener un UFO para la tarjeta
    // Nombro el metodo en mayuscula porque en el
    // fichero proto esta en mayuscula
    public Ufo Dispatch(String owner, String cardNumber) {

        logger.info("Intentaré reservar un UFO para " + owner + " ...");

        CreditCard request = CreditCard.newBuilder()
                                        .setOwner(owner)
                                        .setNumber(cardNumber)
                                        .build();
        Ufo response;
        try {
            response = blockingStub.dispatch(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
        logger.info("Ufo reservado para " + response.getCardNumber() + ": " + response.getId());
        return response;
    }

    // Confirmar el UFO para la tarjeta
    public boolean AssignUfo(String ufoID, String cardNumber) {

        logger.info("Intentare confirmar el UFO " + ufoID + " para " + cardNumber + " ...");

        Ufo request = Ufo.newBuilder()
                            .setId(ufoID)
                            .setCardNumber(cardNumber)
                            .build();

        Processed response;
        try {
            response = blockingStub.assignUfo(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        logger.info("Ufo confirmado " + response.getIsProcessed());
        return response.getIsProcessed();
    }

    void shutDownChannel() throws Exception {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        logger.info("ManagedChannel de UfosParkClient cerrado");
    } 

    /**
     * Main method to run the client as standalone app.
     */
    public static void main(String[] args) throws Exception {
        String user = "Rick";
        String card = "123456789";
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [owner card [target]]");
                System.err.println("");
                System.err.println("  owner   La persona que quiere reservar ek UFO. Por defecto " + user);
                System.err.println("  card    El numero de la tarjeta a la que realizar el cargo. Por defecto " + card);
                System.err.println("  target  El servidor al que conectar. Por defecto " + target);
                System.exit(1);
            }
            user = args[0];
            card = args[1];
        }
        if (args.length > 2) {
            target = args[2];
        }

        // Create a communication channel to the server, known as a Channel. Channels
        // are thread-safe
        // and reusable. It is common to create channels at the beginning of your
        // application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext().build();

        try {
            UfosParkClient client = new UfosParkClient(channel);
            client.Dispatch(user, card);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent
            // leaking these
            // resources the channel should be shut down when it will no longer be used. If
            // it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }


    static UfosParkClient init() {
        
        String target = "localhost:50051";
        
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext().build();

        UfosParkClient ufosParkClient = new UfosParkClient(channel);
        ufosParkClient.setChannel(channel);
        return ufosParkClient;
    }

    private void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }    
}
