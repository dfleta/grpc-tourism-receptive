/**
 * Service Payment
 * business logic and data repo testing
 */

package org.elsmancs.grpc.payment;

import org.junit.Test;
import static org.junit.Assert.*;

import org.elsmancs.grpc.CreditCard;
import org.junit.Before;

public class CardPoolTest {

    private CardPool cards = null;
    private CreditCard card = null; 

    @Before
    public void setup() {
        card = CreditCard.newBuilder()
                            .setOwner("Abradolf Lincler")
                            .setNumber("4916119711304546")
                            .build();
                            
        cards = new CardPool();
        cards.add(card);
        assertEquals(3000, cards.credit(card), 0.1);
    }

    @Test
    public void payTestOK() {
        card = CreditCard.newBuilder()
                            .setOwner("GearHead")
                            .setNumber("8888888888888888")
                            .setCharge(3000)
                            .build();        
        cards.add(card);
        cards.pay(card);
        assertEquals(0, cards.credit(card), 0.1);
    }

    @Test
    public void payTestNOTOK() {
        card = CreditCard.newBuilder()
                            .setOwner("GearHead")
                            .setNumber("8888888888888888")
                            .setCharge(4000)
                            .build();
        cards.add(card);
        cards.pay(card);
        assertEquals(3000, cards.credit(card), 0.1);
    }

    @Test
    public void creditTestOK() {
        card = CreditCard.newBuilder()
                            .setOwner("GearHead")
                            .setNumber("8888888888888888")
                            .setCharge(1000)
                            .build();
        cards.add(card);
        for (int i=0; i<=2; i++) {
            cards.pay(card);
        }
        assertEquals(0, cards.credit(card), 0.1);
    }

}
