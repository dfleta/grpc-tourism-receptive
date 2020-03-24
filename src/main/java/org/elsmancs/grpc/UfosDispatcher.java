package org.elsmancs.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


class UfosDispatcher implements GuestDispatcher {

    private static final Logger logger = Logger.getLogger(UfosDispatcher.class.getName());

    UfosDispatcher() {}

    @Override
    public void dispatch(Card card, ManagedChannel channelPayment) throws Exception {
       
        // comprobar pago autorizado
        // ejecutar llamada al server desde el cliente

        // Puertos de los servicios
        String targetUfosPark = "localhost:50051";

        ManagedChannel channelUfosPark = ManagedChannelBuilder.forTarget(targetUfosPark)
        // Channels are secure by default (via SSL/TLS). 
        // For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build();

        Ufo ufo = null;
        try {
            UfosParkClient client = new UfosParkClient(channelUfosPark);
            ufo = client.Dispatch(card.cardOwner(), card.number());
        } finally {
        // ManagedChannels use resources like threads and TCP connections. 
        // To prevent leaking these resources the channel should be shut down
        // when it will no longer be used. If it may be used
        // again leave it running.
            channelUfosPark.shutdownNow().awaitTermination(5, TimeUnit.SECONDS); // <= dejar corriendo cuando haya más de dos usuarios
        }

        boolean isAuthorised = false;
        PaymentClient client = new PaymentClient(channelPayment);
        isAuthorised = client.Pay(card.cardOwner(), card.number());
        
        if (ufo != null  && isAuthorised) {
            // this.flota.put(ufo.getKey(), card.number());
            logger.info("Aqui llamo al servicio para confirmar reserva UFO");
        }
    }
}