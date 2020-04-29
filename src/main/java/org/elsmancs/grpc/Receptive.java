/**
 * Tourism receptive system
 * implementing observer pattern.
 * 
 * Observer business register interest.
 * A guest is dispatched by charging its credit card
 * with each observer business service cost.
 */
package org.elsmancs.grpc;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

class Receptive {
    
    private Set<GuestDispatcher> observers = new LinkedHashSet<>();

    Receptive() {}

    void register(GuestDispatcher observer) {
        observers.add(observer);
    }

    void dispatch(String cardOwner, String cardNumber) throws Exception {
        for (GuestDispatcher observer: observers) {
                observer.dispatch(cardOwner, cardNumber);       
        }
    }

    @Override
    public String toString() {
        return this.observers.stream()
                                .map(GuestDispatcher::toString)
                                .sorted()
                                .collect(Collectors.toList())
                                .toString();
    }
}