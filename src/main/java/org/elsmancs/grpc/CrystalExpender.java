package org.elsmancs.grpc;


class CrystalExpender {

    private int stock = 0;
    private double itemCost = 50d;

    CrystalExpender() {
        this.stock = 100;
        this.itemCost = 50;
    }

    CrystalExpender(int stock, double itemCost) {
        this.stock = stock;
        this.itemCost = itemCost;
    }

    int dispatch(CreditCard card) {
        return (this.stock > 0)? 1: 0;
    }

    boolean confirm(Crystal crystal) {
        return (this.stock - crystal.getUnidades() > 0)? true: false;
    }

    @Override
    public String toString() {
        return "stock: " + this.stock +
                "\ncost: " + this.itemCost;
    }

    int stock() {
        return this.stock;
    }


    double fee() {
        return this.itemCost;
    }
}