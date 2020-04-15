/**
 * Business logic and data repo 
 * for the Crystal Dispenser service
 */

package org.elsmancs.grpc.crystal;


class CrystalDispenser {

    private int stock = 0;
    private double itemCost = 50d;

    CrystalDispenser() {
        this.stock = 100;
        this.itemCost = 50;
    }

    CrystalDispenser(int stock, double itemCost) {
        this.stock = stock;
        this.itemCost = itemCost;
    }

    // future implement of crystal repo
    // cardNumber needed then 
    int dispatch(String cardNumber) {
        return (this.stock > 0)? 1: 0;
    }

    boolean confirm(int units) {
        if (this.stock - units >= 0) {
            this.stock -= units;
            return true;
        } else {
            return false;
        } 
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