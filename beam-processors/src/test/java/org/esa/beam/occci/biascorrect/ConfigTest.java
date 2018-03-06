package org.esa.beam.occci.biascorrect;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigTest {

    private BiasClimatologyAggregator.Config config;

    @Before
    public void setUp() {
        config = new BiasClimatologyAggregator.Config();
    }

    @Test
    public void testGetName() {
        assertEquals("OC-CCI-BIAS", config.getName());
    }

    @Test
    public void testDefaultConstruction() {
        final String[] varNames = config.getVarNames();
        assertNotNull(varNames);
        assertEquals(0, varNames.length);

        assertEquals(2003, config.getStartYear());
        assertEquals(2007, config.getEndYear());

        assertEquals(Float.NaN, config.getNoDataValue(), 1e-6);
    }

    @Test
    public void testGetVarNames() {
        config.varNames = new String[]{"hip", "hop"};

        final String[] varNames = config.getVarNames();
        assertNotNull(varNames);
        assertEquals(2, varNames.length);
        assertEquals("hip", varNames[0]);
        assertEquals("hop", varNames[1]);
    }

    @Test
    public void testGetNoDataValue() {
        config.noDataValue = -167.89f;

        assertEquals(-167.89f, config.getNoDataValue(), 1e-6);
    }
}

