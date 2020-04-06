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
        ufos.assignUfo("unx", "1111111111111");
        assertTrue(ufos.containsCard("1111111111111"));
        assertEquals("unx", ufos.getUfoOf("1111111111111"));
    }

    @Test
    public void getUfoOfTest() {

        ufos.assignUfo("unx", "1111111111111");
        assertTrue(ufos.containsCard("1111111111111"));
        assertEquals("unx", ufos.getUfoOf("1111111111111"));
        assertFalse(ufos.containsCard("222222222222"));
        assertNull(ufos.getUfoOf("222222222222"));

    }

    @Test
    public void reserveUfoTest() {
        
        String cardNumber = "4916119711304546";

        String ufoID = ufos.reserveUfo(cardNumber);
        assertNotEquals("no ufo reserved", ufoID);

        ufos.assignUfo(ufoID, cardNumber);
        assertTrue(ufos.containsCard(cardNumber));
        
        List<String> cards = ufos.cardNumbers()
                                    .stream()
                                    .filter(n -> n == cardNumber)
                                    .collect(Collectors.toList());

        assertEquals(1, cards.size(), 0);
    }

    @Test
    public void not_reserveUfoTest() {

        String cardNumber = "4916119711304546";
        String ufoID = ufos.reserveUfo(cardNumber);
        ufos.assignUfo(ufoID, "4916119711304546");
        assertTrue(ufos.containsCard(cardNumber));

        ufoID = ufos.reserveUfo(cardNumber);
        assertEquals("no ufo reserved", ufoID);;
        assertTrue(ufos.containsCard(cardNumber));

        List<String> cards = ufos.cardNumbers()
                                    .stream()
                                    .filter(n -> n == cardNumber)
                                    .collect(Collectors.toList());

        assertEquals(1, cards.size(), 0);
    }

    @Test
    public void dispatchNoUfoAvaliableTest() {

        String abradolf = "4916119711304546";
        String ufoID = ufos.reserveUfo(abradolf);
        ufos.assignUfo(ufoID, abradolf);

        String squanchy = "4444444444444444";
        ufoID = ufos.reserveUfo(squanchy);
        ufos.assignUfo(ufoID, squanchy);

        String birdpearson = "1111111111111111"; 
        ufoID = ufos.reserveUfo(birdpearson);
        ufos.assignUfo(ufoID, birdpearson);

        // No quedan UFOs disponibles para Morty
        String morty = "0000000000000000";
        assertEquals("no ufo reserved", ufos.reserveUfo(morty));

        List<String> cards = ufos.cardNumbers()
                                    .stream()
                                    .filter(n -> n == morty)
                                    .collect(Collectors.toList());

        assertEquals(0, cards.size(), 0);
    }
}
