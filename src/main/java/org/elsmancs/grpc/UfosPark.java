package org.elsmancs.grpc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UfosPark {

    private double fee = 500d;
    private final Map<String, String> flota = new HashMap<String, String>();
    
    UfosPark() {
        init();
    }

    private void init() {
        String[] ufosID = { "unx", "dox", "trex" };
		for (String ovni : ufosID) {
			this.add(ovni);
        }
    }

    void add(String ufoID) {
        flota.putIfAbsent(ufoID, null);
    }

    public String reserveUfo(CreditCard card) {

        String ufoID = null;

        if (!flota.containsValue(card.getNumber())) {
            for (Map.Entry<String, String> entry : this.flota.entrySet()) {
                if (entry.getValue() == null) {
                    ufoID = entry.getKey();
                    // this.flota.put(ufoID, card.getNumber());
                    return ufoID;
                }
            }
        }

        return "no ufo reserved";
        
        //if (ufo != null  && card.pay(fee)) {
          //  this.flota.put(ufo.getKey(), card.number());
        //}
    }
    
    // implementar algoritmo ufo blocked
    // por eso dejo el return true
    boolean assignUfo(Ufo ufo) {
        this.flota.put(ufo.getId(), ufo.getCardNumber());
        return true;
    }

    String getUfoOf(String cardNumber) {
        String ufoID = null;
        if (this.flota.containsValue(cardNumber)) {
            for (Map.Entry<String, String> entry: this.flota.entrySet()) {
                if (entry.getValue() == cardNumber) {
                    ufoID = entry.getKey();
                    break;
                }
            }
        }
        return ufoID;
    }

    @Override
    public String toString() {
        String[] ufosID = this.flota.keySet().toArray(new String[flota.size()]);
        Arrays.sort(ufosID);
        return List.of(ufosID).toString();
    }

    /**
     * Testing
     */

    boolean containsCard(String cardNumber) {
        return this.flota.containsValue(cardNumber);
    }

    Collection<String> cardNumbers() {
        return this.flota.values();
    }
} 