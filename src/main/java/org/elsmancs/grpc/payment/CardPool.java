package org.elsmancs.grpc.payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

import org.elsmancs.grpc.CreditCard;

class CardPool {

    private double credit = 3000d;
    private Map<String, Double> cardsPool = new HashMap<String, Double>();

    CardPool() {};

    void add(CreditCard card) {
        this.cardsPool.putIfAbsent(card.getNumber(), credit);
    }

    @VisibleForTesting
    double credit(CreditCard card) {
        return this.cardsPool.get(card.getNumber()).doubleValue();
    }

    boolean pay(CreditCard card) {
        double credit = this.credit(card);
        if (card.getCharge() <= credit) {
            this.cardsPool.put(card.getNumber(), credit -= card.getCharge());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String[] cardNumbers = this.cardsPool.keySet().toArray(new String[cardsPool.size()]);
        return List.of(cardNumbers).toString();
    }
}