package org.elsmancs.grpc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

class UfosPark {

    private final double fee = 2500d;
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

    double fee() {
        return this.fee;
    }

    String reserveUfo(String cardNumber) {

        String ufoID = null;

        if (!flota.containsValue(cardNumber)) {
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
    boolean assignUfo(String ufoID, String cardNumber) {
        if (flota.containsKey(ufoID)) {
            this.flota.put(ufoID, cardNumber);
            return true;
        } else {
            return false;
        }
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

    @VisibleForTesting
    boolean containsCard(String cardNumber) {
        return this.flota.containsValue(cardNumber);
    }

    @VisibleForTesting
    Collection<String> cardNumbers() {
        return this.flota.values();
    }
} 