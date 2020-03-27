
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
public class PaymentClient {

    private static final Logger logger = Logger.getLogger(PaymentClient.class.getName());

    private final PaymentGrpc.PaymentBlockingStub blockingStub;

    /**
     * Construct client for accessing UfosParkServer using the existing channel.
     */
    public PaymentClient(Channel channel) {

        // 'channel' here is a Channel, not a ManagedChannel,
        // so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test
        // and makes it easier to reuse Channels.
        blockingStub = PaymentGrpc.newBlockingStub(channel);
    }

    // Obtener un UFO para la tarjeta
    // Nombro el metodo en mayuscula porque en el
    // fichero proto esta en mayuscula
    public boolean Pay(String owner, String cardNumber) {
        
        logger.info("Intentaré procesar el pago para " + owner + " " + cardNumber + " ...");

        CreditCard request = CreditCard.newBuilder()
                                      .setOwner(owner)
                                      .setNumber(cardNumber)
                                      .build();
        Processed response;
        try {
            response = blockingStub.pay(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        logger.info("Pago procesado :" + response.getIsProcessed() + " para " + request.getOwner() + ": " + request.getNumber());
        return true;
    }


    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        String user = "Rick";
        String card = "123456789";
        // Access a service running on the local machine on port 50061
        String target = "localhost:50061";
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

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();

        try {
            PaymentClient client = new PaymentClient(channel);
            client.Pay(user, card);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public static boolean execute(CreditCard card) throws Exception {
        
        String target = "localhost:50061";
        // Allow passing in the user and target strings as command line arguments        

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();

        try {
            PaymentClient client = new PaymentClient(channel);
            return client.Pay(card.getOwner(), card.getOwner());
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    
}
