package org.elsmancs.grpc.crystal;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Before;

public class CrystalExpenderTest {

    private CrystalExpender expender = null;

    @Before
    public void setupExpender() {
        expender = new CrystalExpender(100, 50.0);
        assertNotNull("Expender creado", expender);
    }

    @Test 
    public void constructortest() {  
        assertNotNull("Expender creado", expender);      
        assertEquals(100, expender.stock());
    }

    @Test
    public void dispatchTestOK() {
        String card = "4916119711304546";
        int units = expender.dispatch(card);
        assertEquals(1, units);
    }

    /**
     * No disponemos de unidades suficientes
     */
    @Test
    public void dispatchTestNoStock() {
        String card = "4916119711304546";
        expender = new CrystalExpender(0, 50.0);
        int units = expender.dispatch(card);
        assertEquals(0, units);
    }

    @Test
    public void confirmTest() {
        boolean isConfirmed = expender.confirm(100);
        assertTrue(isConfirmed);
        assertEquals(0, expender.stock(), 0);
    }

    /**
     * No disponemos de unidades suficientes
     */
    @Test
    public void notConfirmTest() {
        boolean isConfirmed = expender.confirm(200);
        assertFalse(isConfirmed);
        assertEquals(100, expender.stock(), 0);
    }
}