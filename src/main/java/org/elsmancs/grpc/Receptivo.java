
package org.elsmancs.grpc;

import java.util.LinkedHashSet;
import java.util.Set;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

class Receptivo {
    
    private Set<GuestDispatcher> observers = new LinkedHashSet<>();
    private ManagedChannel channelPayment = null;

    Receptivo() {
        this.channelPayment = channelConfig();
    }

    void registra(GuestDispatcher observer) {
        observers.add(observer);
    }

    void dispatch(Card card) throws Exception {
        
        try {
            for (GuestDispatcher observer: observers) {
                observer.dispatch(card, this.channelPayment);       
            }
        } finally {
        // ManagedChannels use resources like threads and TCP connections. 
        // To prevent leaking these resources the channel should be shut down
        // when it will no longer be used. If it may be used
        // again leave it running.
            channelPayment.shutdownNow().awaitTermination(5, TimeUnit.SECONDS); // <= dejar corriendo cuando haya más de dos usuarios
        } 
    }

    private ManagedChannel channelConfig() {

        // Puerto del servicio Payment
        String targetPayment = "localhost:50061";

        // Create a communication channel to the server, known as a Channel. 
        // Channels are thread-safe and reusable. 
        // It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channelPayment = ManagedChannelBuilder.forTarget(targetPayment)
            // Channels are secure by default (via SSL/TLS). 
            // For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();

        return channelPayment;
    }
}