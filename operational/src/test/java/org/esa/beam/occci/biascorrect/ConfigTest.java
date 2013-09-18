package org.esa.beam.occci.biascorrect;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigTest {

    private AggregatorBiasCorrect.Config config;

    @Before
    public void setUp() {
        config = new AggregatorBiasCorrect.Config();
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

        assertEquals(2005, config.getStartYear());
        assertEquals(2010, config.getEndYear());
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
}

