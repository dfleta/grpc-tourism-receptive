package org.elsmancs.grpc.ufos;

import org.elsmancs.grpc.GuestDispatcher;
import org.elsmancs.grpc.Ufo;

import java.util.logging.Logger;
import org.elsmancs.grpc.payment.PaymentClient;


public class UfosDispatcher implements GuestDispatcher {

    private static final Logger logger = Logger.getLogger(UfosDispatcher.class.getName());

    public UfosDispatcher() {}

    @Override
    public void dispatch(String cardOwner, String cardNumber) throws Exception {
        
        // Abrimos canal de comunicacion con el server
        // Los canales son thread-safe and reusable. 
        // Suelen crearse al principio de la app y reutilizarse
        // hasta que finaliza la app.
        UfosParkClient ufosClient = UfosParkClient.init();
        // Llamada al gRPC Dispatch Card para reservar un UFO
        Ufo ufo = ufosClient.Dispatch(cardOwner, cardNumber);

        // Llamada al gRPC Pay para pagar la reserva
        if (ufo != null  && PaymentClient.execute(cardOwner, cardNumber, ufo.getFee())) {
            // Llamada al gRPC para confirmar ese UFO a esa tarjeta
            System.out.println(ufosClient.AssignUfo(ufo.getId(), ufo.getCardNumber()));
        } else {
            logger.info("No hay UFO o credito");
        }

        // El canal se reutiliza entre llamadas al server
        // Cerrarlo al terminar
        ufosClient.shutDownChannel();
    }
}