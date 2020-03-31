package org.elsmancs.grpc;

public class App {
    
    public static void main(String[] args) throws Exception {

        Receptivo receptivo = new Receptivo();

        receptivo.registra(new UfosDispatcher());
        receptivo.registra(new CrystalDispatcher());

        String cardOwner = "Rick";
        String cardNumber = "123456789";
        // owner y cardNumber como argumentos en línea de comandos
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [owner card [target]]");
                System.err.println("");
                System.err.println("  owner   La persona que quiere reservar el UFO.");
                System.err.println("  card    El numero de la tarjeta a la que realizar el cargo.");
                System.exit(1);
            }                               
            cardOwner = args[0];
            cardNumber = args[1];
        }

        receptivo.dispatch(cardOwner, cardNumber);
    }
}