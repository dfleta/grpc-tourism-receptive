
/**
 * Client side Streaming RPC
 */

package org.elsmancs.grpc.payment;

import org.elsmancs.grpc.CreditCard;
import org.elsmancs.grpc.PaymentGrpc;
import org.elsmancs.grpc.Processed;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests authorisation from the {@link PaymentServer}.
 */
public class PaymentClient {

    private static final Logger logger = Logger.getLogger(PaymentClient.class.getName());

    private final PaymentGrpc.PaymentBlockingStub blockingStub;

    /**
     * Construct client for accessing PaymentServer using the existing channel.
     */
    public PaymentClient(Channel channel) {

        // gRPC examples comments:
        // 'channel' here is a Channel, not a ManagedChannel,
        // so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test
        // and makes it easier to reuse Channels.

        blockingStub = PaymentGrpc.newBlockingStub(channel);
    }

    /**
     * Obtain payment authorisation
     */
    public boolean Pay(String owner, String cardNumber, double fee) {
        
        logger.info("Processing payment for " + owner + " " + cardNumber + " ...");

        CreditCard request = CreditCard.newBuilder()
                                      .setOwner(owner)
                                      .setNumber(cardNumber)
                                      .setCharge(fee)
                                      .build();
        Processed response;
        try {
            response = blockingStub.pay(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        logger.info("Charge processed :" + response.getIsProcessed() 
                    + " for " + request.getOwner() 
                    + ": " + request.getNumber());
        return response.getIsProcessed();
    }

    /**
     * Payment Client setup
     * and call to Pay gRPC
     */
    public static boolean execute(String cardOwner, String cardNumber, double charge) throws Exception {
        
        String target = "localhost:50061";

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            .usePlaintext()
            .build();

        try {
            PaymentClient client = new PaymentClient(channel);
            return client.Pay(cardOwner, cardNumber, charge);
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Main method to run the client as a standalone app.
     */
    public static void main(String[] args) throws Exception {
        String user = "Rick";
        String card = "123456789";
        double charge = 500d;
        // Access a service running on the local machine on port 50061
        String target = "localhost:50061";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
            System.err.println("Usage: [owner card [target]]");
            System.err.println("");
            System.err.println("  owner   Card owner. Default " + user);
            System.err.println("  card    Card number. Default " + card);
            System.err.println("  target  Server to connect to. Default " + target);
            System.exit(1);
            }
            user = args[0];
            card = args[1];
        }
        if (args.length > 2) {
            target = args[2];
        }

        // gRPC documentation examples comments:
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // gRPC documentation examples comments:
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();

        try {
            PaymentClient client = new PaymentClient(channel);
            client.Pay(user, card, charge);
        } finally {
            // gRPC documentation examples comments:
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
