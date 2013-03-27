package org.esa.beam.occci.qaa;


import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.ProductUtils;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class QaaOpTest {

    @Test
    public void testOperator() throws Exception {
        QaaOp qaaOp = new QaaOp();
        qaaOp.setParameter("validPixelExpression", "true");
        qaaOp.setParameter("divideByPI", "false");
        qaaOp.setParameter("reflectanceBandNames", "Rrs412,Rrs443,Rrs490,Rrs510,Rrs560,Rrs620");
        qaaOp.setSourceProduct(createTestProduct(2, 2));

        Product targetProduct = qaaOp.getTargetProduct();
        assertNotNull(targetProduct);

        String[] expectedBandNames = {"a_total_413", "a_total_443", "a_total_490", "a_total_510", "a_total_560", "bb_spm_413", "bb_spm_443", "bb_spm_490", "bb_spm_510", "bb_spm_560", "a_pig_413", "a_pig_443", "a_pig_490", "a_ys_413", "a_ys_443", "a_ys_490"};
        assertEquals(expectedBandNames.length, targetProduct.getNumBands());
        assertArrayEquals(expectedBandNames, targetProduct.getBandNames());

        assertEquals(0.0326309, getSample(targetProduct, "a_total_413"), 1e-6);
        assertEquals(0.0264908, getSample(targetProduct, "a_total_443"), 1e-6);
        assertEquals(0.0289172, getSample(targetProduct, "a_total_490"), 1e-6);
        assertEquals(0.0455736, getSample(targetProduct, "a_total_510"), 1e-6);
        assertEquals(0.0657829, getSample(targetProduct, "a_total_560"), 1e-6);

        assertEquals(0.0205040, getSample(targetProduct, "bb_spm_413"), 1e-6);
        assertEquals(0.0171448, getSample(targetProduct, "bb_spm_443"), 1e-6);
        assertEquals(0.0133822, getSample(targetProduct, "bb_spm_490"), 1e-6);
        assertEquals(0.0121625, getSample(targetProduct, "bb_spm_510"), 1e-6);
        assertEquals(0.00977813, getSample(targetProduct, "bb_spm_560"), 1e-6);

        assertEquals(0.00263956, getSample(targetProduct, "a_pig_413"), 1e-6);
        assertEquals(0.00337634, getSample(targetProduct, "a_pig_443"), 1e-6);
        assertEquals(0.00614989, getSample(targetProduct, "a_pig_490"), 1e-6);

        assertEquals(0.0254953, getSample(targetProduct, "a_ys_413"), 1e-6);
        assertEquals(0.0160453, getSample(targetProduct, "a_ys_443"), 1e-6);
        assertEquals(0.0077673, getSample(targetProduct, "a_ys_490"), 1e-6);
    }

    private double getSample(Product targetProduct, String bandName) {
        return ProductUtils.getGeophysicalSampleDouble(targetProduct.getBand(bandName), 1, 1, 0);
    }

    private static Product createTestProduct(int w, int h) {
        final float[] rrs_in = {0.030262154f, 0.031086152f, 0.022717977f, 0.013177891f, 0.0072450927f, 0.0028870495f, 0.0024475828f};
        Product testProduct = new Product("p", "t", w, h);
        addBand(testProduct, "Rrs412", rrs_in[0]);
        addBand(testProduct, "Rrs443", rrs_in[1]);
        addBand(testProduct, "Rrs490", rrs_in[2]);
        addBand(testProduct, "Rrs510", rrs_in[3]);
        addBand(testProduct, "Rrs560", rrs_in[4]);
        addBand(testProduct, "Rrs620", rrs_in[5]);
        addBand(testProduct, "Rrs665", rrs_in[6]);
        return testProduct;
    }

    private static void addBand(Product product, String name, float value) {
        Band band = product.addBand(name, ProductData.TYPE_FLOAT32);
        float[] floatValues = new float[(int) band.getNumDataElems()];
        Arrays.fill(floatValues, value);
        band.setData(ProductData.createInstance(floatValues));
    }
}
