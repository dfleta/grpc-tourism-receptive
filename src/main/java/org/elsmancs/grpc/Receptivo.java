
package org.elsmancs.grpc;

import java.util.LinkedHashSet;
import java.util.Set;

class Receptivo {
    
    private Set<GuestDispatcher> observers = new LinkedHashSet<>();

    Receptivo() {}

    void registra(GuestDispatcher observer) {
        observers.add(observer);
    }

    void dispatch(String cardOwner, String cardNumber) throws Exception {
        for (GuestDispatcher observer: observers) {
                observer.dispatch(cardOwner, cardNumber);       
        }
    }
}