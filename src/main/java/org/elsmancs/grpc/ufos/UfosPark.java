/**
 * UFOs service
 * business logic and data repo 
 */
package org.elsmancs.grpc.ufos;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

class UfosPark {

    private final double fee = 2500d;
    private final Map<String, String> fleet = new HashMap<String, String>();
    
    UfosPark() {
        init();
    }

    private void init() {
        String[] ufosID = { "unx", "dox", "trex" };
		for (String ufo : ufosID) {
			this.add(ufo);
        }
    }

    void add(String ufoID) {
        fleet.putIfAbsent(ufoID, null);
    }

    double fee() {
        return this.fee;
    }

    String reserveUfo(String cardNumber) {

        String ufoID = null;

        if (!fleet.containsValue(cardNumber)) {
            for (Map.Entry<String, String> entry : this.fleet.entrySet()) {
                if (entry.getValue() == null) {
                    ufoID = entry.getKey();
                    return ufoID;
                }
            }
        }

        return "no ufo reserved";
    }
    
    // Implementar algoritmo ufo blocked
    boolean assignUfo(String ufoID, String cardNumber) {
        if (fleet.containsKey(ufoID)) {
            this.fleet.put(ufoID, cardNumber);
            return true;
        } else {
            return false;
        }
    }

    String getUfoOf(String cardNumber) {
        String ufoID = null;
        if (this.fleet.containsValue(cardNumber)) {
            for (Map.Entry<String, String> entry: this.fleet.entrySet()) {
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
        String[] ufosID = this.fleet.keySet().toArray(new String[fleet.size()]);
        Arrays.sort(ufosID);
        return List.of(ufosID).toString();
    }

    /**
     * Testing
     */

    @VisibleForTesting
    boolean containsCard(String cardNumber) {
        return this.fleet.containsValue(cardNumber);
    }

    @VisibleForTesting
    Collection<String> cardNumbers() {
        return this.fleet.values();
    }
} 