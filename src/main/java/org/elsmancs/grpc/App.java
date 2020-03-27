package org.elsmancs.grpc;

public class App {
    
    public static void main(String[] args) throws Exception {

        Receptivo receptivo = new Receptivo();

        receptivo.registra(new UfosDispatcher());

        CreditCard card = null;
        // owner y cardNumber como argumentos en línea de comandos
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [owner card [target]]");
                System.err.println("");
                System.err.println("  owner   La persona que quiere reservar el UFO.");
                System.err.println("  card    El numero de la tarjeta a la que realizar el cargo.");
                System.exit(1);
            }
            card = CreditCard.newBuilder()
                                .setOwner(args[0])
                                .setNumber(args[1])
                                .build();
                                
            new Card(args[0], args[1]);
        }

        receptivo.dispatch(card);
    }
}