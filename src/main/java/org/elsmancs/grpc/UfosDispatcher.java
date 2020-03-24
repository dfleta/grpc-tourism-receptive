package org.elsmancs.grpc;

import java.util.logging.Logger;


class UfosDispatcher implements GuestDispatcher {

    private static final Logger logger = Logger.getLogger(UfosDispatcher.class.getName());

    UfosDispatcher() {}

    @Override
    public void dispatch(Card card) throws Exception {
       
        // comprobar pago autorizado
        // ejecutar llamada al server desde el cliente
        
        // tengo que cambiar este UFO por Ufo.execute
        if (UfosParkClient.execute(card) != null  && PaymentClient.execute(card)) {
            // this.flota.put(ufo.getKey(), card.number());
            logger.info("Aqui llamo al servicio para confirmar reserva UFO");
        }
    }
}