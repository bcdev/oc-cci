package org.esa.beam.occci.util.binning;

import org.esa.beam.binning.support.VariableContextImpl;
import org.esa.beam.binning.support.VectorImpl;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class BinningUtilsTest {

    @Test
    public void testGetBandIndices_directMapping()  {
        final String[] bandNames = {"band_1", "band_2", "band_3"};

        final VariableContextImpl context = new VariableContextImpl();
        context.defineVariable("band_1");
        context.defineVariable("band_2");
        context.defineVariable("band_3");

        int[] indices = BinningUtils.getBandIndices(context, bandNames);
        assertEquals(3, indices.length);
        assertEquals(0, indices[0]);
        assertEquals(1, indices[1]);
        assertEquals(2, indices[2]);
    }

    @Test
    public void testGetBandIndices_mixedMapping()  {
        final String[] bandNames = {"band_4", "band_1", "band_3", "band_2"};

        final VariableContextImpl context = new VariableContextImpl();
        context.defineVariable("band_2");
        context.defineVariable("band_3");
        context.defineVariable("band_1");
        context.defineVariable("band_4");

        int[] indices = BinningUtils.getBandIndices(context, bandNames);
        assertEquals(4, indices.length);
        assertEquals(3, indices[0]);
        assertEquals(2, indices[1]);
        assertEquals(1, indices[2]);
        assertEquals(0, indices[3]);
    }

    @Test
    public void testGetBandIndices_bandNotPresent()  {
        final String[] bandNames = {"band_1", "band_2", "band_3"};

        final VariableContextImpl context = new VariableContextImpl();
        context.defineVariable("band_1");
        context.defineVariable("band_3");

        try {
            BinningUtils.getBandIndices(context, bandNames);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testSetToInvalid() {
        final VectorImpl vector = new VectorImpl(new float[4]);

        BinningUtils.setToInvalid(vector);

        for (int i = 0; i < 4; i++) {
            assertThat(vector.get(i), is(Float.NaN));
        }
    }
}
