package org.elsmancs.grpc;

class Card {

    private final String owner;
    private final String number;
    private double credit = 3000d;
    private final String SYMBOL = "EZI";

    Card(String owner, String number) {
        this.owner = owner;
        this.number = number;
    }

    boolean pay(double charge) {
        if (charge <= this.credit) {
            this.credit -= charge;
            return true;
        } else {
            return false;
        }
    }

    String number() {
        return this.number;
    }

    String cardOwner() {
        return this.owner;
    }

    double credit() {
        return this.credit;
    }

    @Override
    public String toString() {
        return "owner: " + this.owner 
                + "\nnumber: " + this.number
                + "\ncredit: " + this.credit + this.SYMBOL;
    }
}