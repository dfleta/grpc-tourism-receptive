package org.elsmancs.grpc;

import org.elsmancs.grpc.crystal.CrystalDispatcher;
import org.elsmancs.grpc.ufos.UfosDispatcher;

public class App {
    
    public static void main(String[] args) throws Exception {

        Receptive receptive = new Receptive();

        receptive.register(new UfosDispatcher());
        receptive.register(new CrystalDispatcher());

        String cardOwner = "Rick";
        String cardNumber = "123456789";
        // owner y cardNumber como argumentos en línea de comandos
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [owner card [target]]");
                System.err.println("");
                System.err.println("  owner   Guest to charge for the services. Default " + cardOwner);
                System.err.println("  card    Card number to charge for. Default " + cardNumber);
                System.exit(1);
            }                               
            cardOwner = args[0];
            cardNumber = args[1];
        }

        receptive.dispatch(cardOwner, cardNumber);
    }
}