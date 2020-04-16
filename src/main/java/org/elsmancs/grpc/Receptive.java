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
}