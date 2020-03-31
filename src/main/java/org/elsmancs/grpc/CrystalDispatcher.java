package org.elsmancs.grpc;

import java.util.logging.Logger;


class CrystalDispatcher implements GuestDispatcher {

    private static final Logger logger = Logger.getLogger(CrystalDispatcher.class.getName());

    CrystalDispatcher() {}

    @Override
    public void dispatch(String cardOwner, String cardNumber) throws Exception {
               
        // Abrimos canal con el server
        CrystalClient crystalClient = CrystalClient.init();
        // Llamada al gRPC Dispatch Card para reservar un UFO
        Crystal crystal = crystalClient.Dispatch(cardOwner, cardNumber);

        // Llamada al gRPC Pay para pagar el crystal
        if (crystal != null  && PaymentClient.execute(cardOwner, cardNumber, crystal.getFee())) {
            // this.flota.put(ufo.getKey(), card.number());
            logger.info("Llamada al servicio para confirmar unidades");
            // Llamada al gRPC para confirmar ese UFO a esa tarjeta
            System.out.println(crystalClient.Confirm(crystal));
        } else {
            logger.info("No hay crytal o credito");
        }

        // El canal se reutilizan entre llamadas al server
        // Cerrarlo al terminar
        crystalClient.shutDownChannel();
        
    }
}