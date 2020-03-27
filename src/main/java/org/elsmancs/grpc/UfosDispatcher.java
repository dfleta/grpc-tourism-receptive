package org.elsmancs.grpc;

import java.util.logging.Logger;


class UfosDispatcher implements GuestDispatcher {

    private static final Logger logger = Logger.getLogger(UfosDispatcher.class.getName());

    UfosDispatcher() {}

    @Override
    public void dispatch(CreditCard card) throws Exception {
        
        // Abrimos canal con el server
        UfosParkClient ufosClient = UfosParkClient.init();
        // Llamada al gRPC Dispatch Card para reservar un UFO
        Ufo ufo = ufosClient.Dispatch(card.getOwner(), card.getNumber());

        // Llamada al gRPC Pay para pagar la reserva
        if (ufo != null  && PaymentClient.execute(card)) {
            // this.flota.put(ufo.getKey(), card.number());
            logger.info("Aqui llamo al servicio para confirmar reserva UFO");
            // Llamada al gRPC para confirmar ese UFO a esa tarjeta
            System.out.println(ufosClient.AssignUfo(ufo.getId(), ufo.getCardNumber()));
        }

        // El canal se reutilizan entre llamadas al server
        // Cerrarlo al terminar
        ufosClient.shutDownChannel();
        
    }
}