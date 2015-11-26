package org.esa.beam.occci.qaa;


import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.util.ProductUtils;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class QaaOpTest {

    @Test
    public void testOperator() throws Exception {
        QaaOp qaaOp = new QaaOp();
        qaaOp.setParameterDefaultValues();
        qaaOp.setParameter("validPixelExpression", "true");
        qaaOp.setParameter("divideByPI", "false");
        qaaOp.setParameter("reflectanceBandNames", "Rrs412,Rrs443,Rrs490,Rrs510,Rrs560,Rrs620");
        qaaOp.setSourceProduct(createTestProduct(2, 2));

        Product targetProduct = qaaOp.getTargetProduct();
        assertNotNull(targetProduct);

        String[] expectedBandNames = {
                "atot_413", "atot_443", "atot_490", "atot_510", "atot_560", "atot_665",
                "bbp_413", "bbp_443", "bbp_490", "bbp_510", "bbp_560", "bbp_665",
                "aph_413", "aph_443", "aph_490", "aph_510", "aph_560", "aph_665",
                "adg_413", "adg_443", "adg_490", "adg_510", "adg_560", "adg_665"
        };
        assertEquals(expectedBandNames.length, targetProduct.getNumBands());
        assertArrayEquals(expectedBandNames, targetProduct.getBandNames());

        assertEquals(0.0326309, getSample(targetProduct, "atot_413"), 1e-6);
        assertEquals(0.0264908, getSample(targetProduct, "atot_443"), 1e-6);
        assertEquals(0.0289172, getSample(targetProduct, "atot_490"), 1e-6);
        assertEquals(0.0455736, getSample(targetProduct, "atot_510"), 1e-6);
        assertEquals(0.0657829, getSample(targetProduct, "atot_560"), 1e-6);
        assertEquals(0.109646804, getSample(targetProduct, "atot_665"), 1e-6);

        assertEquals(0.014772, getSample(targetProduct, "bbp_413"), 1e-6);
        assertEquals(0.0128989, getSample(targetProduct, "bbp_443"), 1e-6);
        assertEquals(0.0106138, getSample(targetProduct, "bbp_490"), 1e-6);
        assertEquals(0.0098237, getSample(targetProduct, "bbp_510"), 1e-6);
        assertEquals(0.00819854, getSample(targetProduct, "bbp_560"), 1e-6);
        assertEquals(0.00588059, getSample(targetProduct, "bbp_665"), 1e-6);

        assertEquals(0.00263956, getSample(targetProduct, "aph_413"), 1e-6);
        assertEquals(0.00337634, getSample(targetProduct, "aph_443"), 1e-6);
        assertEquals(0.00614989, getSample(targetProduct, "aph_490"), 1e-6);
        assertEquals(0.00736940, getSample(targetProduct, "aph_510"), 1e-6);
        assertEquals(0.00124655, getSample(targetProduct, "aph_560"), 1e-6);
        assertEquals(-0.3198745, getSample(targetProduct, "aph_665"), 1e-6);

        assertEquals(0.0254953, getSample(targetProduct, "adg_413"), 1e-6);
        assertEquals(0.0160453, getSample(targetProduct, "adg_443"), 1e-6);
        assertEquals(0.0077673, getSample(targetProduct, "adg_490"), 1e-6);
        assertEquals(0.0057041, getSample(targetProduct, "adg_510"), 1e-6);
        assertEquals(0.0026363, getSample(targetProduct, "adg_560"), 1e-6);
        assertEquals(0.0005213, getSample(targetProduct, "adg_665"), 1e-6);
    }

    @Test
    public void testValidateSourceProduct_valid() {
        final Product product = createTestProduct(2, 2);
        final String[] bandNames = {"Rrs412", "Rrs443", "Rrs490", "Rrs510", "Rrs560", "Rrs620", "Rrs665"};

        QaaOp.validateSourceProduct(bandNames, product);
    }

    @Test
    public void testValidateSourceProduct_invalid() {
        final Product product = createTestProduct(2, 2);
        final String[] bandNames = {"schnick", "schnack", "schnuck"};

        try {
            QaaOp.validateSourceProduct(bandNames, product);
            fail("OperatorException expected");
        } catch (OperatorException expected) {
        }
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
