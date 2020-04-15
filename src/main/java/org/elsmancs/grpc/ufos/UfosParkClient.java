
/**
 * Client side Streaming RPC
 */

package org.elsmancs.grpc.ufos;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Processed;
import org.elsmancs.grpc.Ufo;
import org.elsmancs.grpc.UfosParkGrpc;

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

        // gRPC example comments: 
        // 'channel' here is a Channel, not a ManagedChannel,
        // so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test
        // and makes it easier to reuse Channels.
        blockingStub = UfosParkGrpc.newBlockingStub(channel);
    }

    /**
     * Get an avaliable UFO for the card. 
     */
    public Ufo Dispatch(String owner, String cardNumber) {

        logger.info("Dispatching an UFO to " + owner + " ...");

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
        logger.info("Ufo reserved for " + response.getCardNumber() + ": " + response.getId());
        return response;
    }

    /**
     * Confirm the UFO for the card.
     */ 
    public boolean AssignUfo(String ufoID, String cardNumber) {

        logger.info("Confirming UFO " + ufoID + " for " + cardNumber + " ...");

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
        logger.info("Ufo confirmed " + response.getIsProcessed());
        return response.getIsProcessed();
    }

    /**
     * Setup the client
     */
    static UfosParkClient init() {
        
        String target = "localhost:50051";
        
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Los canales son seguros por defecto (via SSL/TLS). 
                // Deshabilitamos TLS para evitar la necesidad de certificados.
                .usePlaintext().build();

        UfosParkClient ufosParkClient = new UfosParkClient(channel);
        ufosParkClient.setChannel(channel);
        return ufosParkClient;
    }


    private void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }    


    void shutDownChannel() throws Exception {
        // ManagedChannels usan recursos como threads y conexiones TCP. 
        // Es necesario cerrarlos cuando no vayan a ser usados.
        // Si va a ser usado de nuevo puede dejarse corriendo.
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        logger.info("ManagedChannel de UfosParkClient cerrado");
    }

    /**
     * Main method to run the client as a standalone app.
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
                System.err.println("  owner   Person who books the UFO. Default " + user);
                System.err.println("  card    Card number to pay for the UFO. Default " + card);
                System.err.println("  target  Server to connect to. Default " + target);
                System.exit(1);
            }
            user = args[0];
            card = args[1];
        }
        if (args.length > 2) {
            target = args[2];
        }

        // gRPC examples comments:
        // Crear un canal de comunicacion con el servidor, llamado Channel. 
        // Channels are thread-safe and reusable. It is common to create channels 
        // at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Los canales son seguros por defecto (via SSL/TLS). 
                // Deshabilitamos TLS para evitar la necesidad de certificados.
                .usePlaintext().build();

        try {
            UfosParkClient client = new UfosParkClient(channel);
            client.Dispatch(user, card);
        } finally {
            // ManagedChannels usan recursos como threads y conexiones TCP. 
            // Es necesario cerrarlos cuando no vayan a ser usados.
            // Si va a ser usado de nuevo puede dejarse corriendo.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }    
}
