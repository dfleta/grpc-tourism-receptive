package org.elsmancs.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;


public class Dispatcher {

    public static void main(String[] args) throws Exception {

        String user = "";
        String card = "";

        // Puertos de los servicios
        String targetUfosPark = "localhost:50051";
        
        // Onwr y Card como argumentos en línea de comandos
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [owner card [target]]");
                System.err.println("");
                System.err.println("  owner   La persona que quiere reservar ek UFO. Por defecto " + user);
                System.err.println("  card    El numero de la tarjeta a la que realizar el cargo. Por defecto " + card);
                System.exit(1);
            }
            user = args[0];
            card = args[1];
        }

        // Create a communication channel to the server, known as a Channel. 
        // Channels are thread-safe and reusable. 
        // It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channelUfosPark = ManagedChannelBuilder.forTarget(targetUfosPark)
            // Channels are secure by default (via SSL/TLS). 
            // For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();

        try {
            UfosParkClient client = new UfosParkClient(channelUfosPark);
            client.Dispatch(user, card);
        } finally {
        // ManagedChannels use resources like threads and TCP connections. 
        // To prevent leaking these resources the channel should be shut down
        // when it will no longer be used. If it may be used
        // again leave it running.
            channelUfosPark.shutdownNow().awaitTermination(5, TimeUnit.SECONDS); // <= dejar corriendo cuando haya más de dos usuarios
        }
    }

}
