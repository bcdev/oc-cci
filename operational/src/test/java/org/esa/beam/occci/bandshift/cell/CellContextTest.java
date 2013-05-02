package org.esa.beam.occci.bandshift.cell;


import org.esa.beam.occci.cell.CellContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CellContextTest {

    private CellContext cellContext;

    @Before
    public void setUp() {
        cellContext = new CellContext();
    }

    @Test
    public void testDefaultConstruction() {
        assertEquals(0, cellContext.getFeatureCount());
    }

    @Test
    public void testAddFeature() {
        cellContext.addFeature("name");

        assertEquals(1, cellContext.getFeatureCount());
    }
}
