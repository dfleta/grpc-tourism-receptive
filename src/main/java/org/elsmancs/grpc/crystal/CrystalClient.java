
/**
 * Client side Streaming RPC
 */

package org.elsmancs.grpc.crystal;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.Crystal;
import org.elsmancs.grpc.CrystalExpenderGrpc;
import org.elsmancs.grpc.Processed;

/**
 * A simple client that requests Kalaxian crytal from the {@link CrystalServer}.
 */
public class CrystalClient {

    private static final Logger logger = Logger.getLogger(CrystalClient.class.getName());

    private final CrystalExpenderGrpc.CrystalExpenderBlockingStub blockingStub;

    // SRP + OCP
    private ManagedChannel channel = null;


    /**
     * Construct client for accessing CrystalServer using the existing channel.
     */
    public CrystalClient(Channel channel) {

        // gRPC examples comments:
        // 'channel' here is a Channel, not a ManagedChannel,
        // so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test
        // and makes it easier to reuse Channels.
        blockingStub = CrystalExpenderGrpc.newBlockingStub(channel);
    }

    /**
     * Find available crystal for a credit card
     */
    Crystal Dispatch(String owner, String cardNumber) {

        logger.info("Checking avalible Kalaxian Crystal for " + owner + " ...");

        CreditCard request = CreditCard.newBuilder()
                                        .setOwner(owner)
                                        .setNumber(cardNumber)
                                        .build();
        Crystal response;
        try {
            response = blockingStub.dispatch(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
        logger.info(response.getUnidades() + " units of Kalaxian Crystal avaliable for " + request.getOwner() + ": " + request.getNumber());
        return response;
    }

    /**
     * Find available crystal for a credit card
     */
    boolean Confirm(int unidades) {

        logger.info("Dispensing " + unidades + " crystal units" + " ...");

        Crystal request = Crystal.newBuilder()
                            .setUnidades(unidades)
                            .build();

        Processed response;
        try {
            response = blockingStub.confirm(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        logger.info(unidades + " crystal units dispensed " + response.getIsProcessed());
        return response.getIsProcessed();
    }

    /**
     * Setup the client.
     * Static factory. Not recommended, 
     * but assuming that to encapsulate the target and 
     * channel configuration code is a compelling reason.
     * Item 17: Minimize mutability, Effective Java, Joshua Bloch.
     */
    static CrystalClient init() {
        
        String target = "localhost:50071";
        
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). 
                // For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext().build();

        CrystalClient crystalClient = new CrystalClient(channel);
        crystalClient.setChannel(channel);
        return crystalClient;
    }

    private void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    void shutDownChannel() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        logger.info("ManagedChannel CrystalClient closed");
    }


    /**
     * Main method to run the client as standalone app.
     */
    public static void main(String[] args) throws Exception {
        String user = "Rick";
        String card = "123456789";
        // Access a service running on the local machine on port 50071
        String target = "localhost:50071";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [owner card [target]]");
                System.err.println("");
                System.err.println("  owner   Person who pays for the crystal. Default " + user);
                System.err.println("  card    Card number to pay for the crstal. Default " + card);
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
            CrystalClient client = new CrystalClient(channel);
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
}
