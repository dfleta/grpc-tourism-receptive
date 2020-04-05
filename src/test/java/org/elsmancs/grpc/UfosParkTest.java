package org.elsmancs.grpc;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;

public class UfosParkTest {

    UfosPark ufos = null;
    String[] ovnis = { "unx", "dox", "trex" };

    @Before
    public void setupUfosPark() {
        ufos = new UfosPark();
        assertNotNull("Parque de UFOS creados", ufos);
        for (String ovni : ovnis) {
			ufos.add(ovni);
        }
    }

    @Test
    public void addUfoTest() {
        Arrays.sort(ovnis);
        assertEquals(List.of(ovnis).toString(), ufos.toString());
        
        List<String> cards = ufos.cardNumbers()
                                .stream()
                                .collect(Collectors.toList());
        assertEquals(ovnis.length, cards.size(), 0);
    }

    @Test
    public void assignUfoTest() {
        Ufo ufo = Ufo.newBuilder().setId("unox").setCardNumber("1111").build();
        ufos.assignUfo(ufo);
        assertTrue(ufos.containsCard("1111"));
        assertEquals("unox", ufos.getUfoOf("1111"));
    }

    @Test
    public void getUfoOfTest() {
        
        Ufo ufo = Ufo.newBuilder().setId("unox").setCardNumber("1111").build();
        ufos.assignUfo(ufo);
        assertTrue(ufos.containsCard("1111"));
        assertEquals("unox", ufos.getUfoOf("1111"));
        assertFalse(ufos.containsCard("2222"));
        assertNull(ufos.getUfoOf("2222"));

    }

    @Test
    public void reserveUfoTest() {
        
        CreditCard card = CreditCard.newBuilder().setOwner("Abradolf Lincler").setNumber("4916119711304546").build();
        String ufoID = ufos.reserveUfo(card);
        assertNotEquals("no ufo reserved", ufoID);

        ufos.assignUfo(Ufo.newBuilder().setId(ufoID).setCardNumber("4916119711304546").build());
        assertTrue(ufos.containsCard(card.getNumber()));
        
        // ESTO A UNA REGLA??
        List<String> cards = ufos.cardNumbers()
                                    .stream()
                                    .filter(n -> n == card.getNumber())
                                    .collect(Collectors.toList());

        assertEquals(1, cards.size(), 0);
    }

    @Test
    public void not_reserveUfoTest() {

        CreditCard card = CreditCard.newBuilder().setOwner("Abradolf Lincler").setNumber("4916119711304546").build();
        String ufoID = ufos.reserveUfo(card);
        ufos.assignUfo(Ufo.newBuilder().setId(ufoID).setCardNumber("4916119711304546").build());

        ufoID = ufos.reserveUfo(card);

        assertEquals("no ufo reserved", ufoID);;
        assertTrue(ufos.containsCard(card.getNumber()));

        // ESTO A UNA REGLA
        List<String> cards = ufos.cardNumbers()
                                    .stream()
                                    .filter(n -> n == card.getNumber())
                                    .collect(Collectors.toList());

        assertEquals(1, cards.size(), 0);
    }

    @Test
    public void dispatchNoUfoAvaliableTest() {

        CreditCard card = CreditCard.newBuilder().setOwner("Abradolf Lincler").setNumber("4916119711304546").build();
        String ufoID = ufos.reserveUfo(card);

        CreditCard squanchy = CreditCard.newBuilder().setOwner("Squanchy").setNumber("4444444444444444").build();
        ufoID = ufos.reserveUfo(squanchy);

        CreditCard birdpearson = CreditCard.newBuilder().setOwner("Birdpearson").setNumber("1111111111111111").build(); 
        ufoID = ufos.reserveUfo(birdpearson);

        CreditCard morty = CreditCard.newBuilder().setOwner("Morty").setNumber("0000000000000000").build();
        ufoID = ufos.reserveUfo(morty);

        List<String> cards = ufos.cardNumbers()
                                    .stream()
                                    .filter(n -> n == morty.getNumber())
                                    .collect(Collectors.toList());

        assertEquals(0, cards.size(), 0);
    }
}
