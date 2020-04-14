package org.elsmancs.grpc.crystal;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Before;

public class CrystalDispenserTest {

    private CrystalDispenser dispenser = null;

    @Before
    public void setupExpender() {
        dispenser = new CrystalDispenser(100, 50.0);
        assertNotNull("Expender creado", dispenser);
    }

    @Test 
    public void constructortest() {  
        assertNotNull("Expender creado", dispenser);      
        assertEquals(100, dispenser.stock());
    }

    @Test
    public void dispatchTestOK() {
        String card = "4916119711304546";
        int units = dispenser.dispatch(card);
        assertEquals(1, units);
    }

    /**
     * No disponemos de unidades suficientes
     */
    @Test
    public void dispatchTestNoStock() {
        String card = "4916119711304546";
        dispenser = new CrystalDispenser(0, 50.0);
        int units = dispenser.dispatch(card);
        assertEquals(0, units);
    }

    @Test
    public void confirmTest() {
        boolean isConfirmed = dispenser.confirm(100);
        assertTrue(isConfirmed);
        assertEquals(0, dispenser.stock(), 0);
    }

    /**
     * No disponemos de unidades suficientes
     */
    @Test
    public void notConfirmTest() {
        boolean isConfirmed = dispenser.confirm(200);
        assertFalse(isConfirmed);
        assertEquals(100, dispenser.stock(), 0);
    }
}