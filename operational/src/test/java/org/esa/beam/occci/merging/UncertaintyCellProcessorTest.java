package org.esa.beam.occci.merging;

import org.junit.Ignore;
import org.junit.Test;

import static org.esa.beam.occci.merging.UncertaintyCellProcessor.computeClassificationSum;
import static org.esa.beam.occci.merging.UncertaintyCellProcessor.computeUncertainty;
import static org.junit.Assert.assertEquals;

public class UncertaintyCellProcessorTest {

    private static final float[] WATER_VALUES = new float[]{
            5.04E-007f,
            0.043338194f,
            0.77421f,
            4.51E-006f,
            7.38E-020f,
            4.31E-006f,
            2.60E-004f,
            1.34E-006f,
            4.29E-007f
    };

    @Test
    public void testClassificationSum() throws Exception {
        double classificationSum = computeClassificationSum(WATER_VALUES);
        assertEquals(0.8178188, classificationSum, 1e-6);
    }
    
    @Test
    public void testChlor_a() throws Exception {
        double classificationSum = computeClassificationSum(WATER_VALUES);

        double[] chlaRmsTable = {0.205403, 0.235605, 0.267787, 0.315107, 0.305332, 0.405685, 0.326042, 0.334740};
        double chlaRms = computeUncertainty(chlaRmsTable, classificationSum, WATER_VALUES);
        assertEquals(0.2661012, chlaRms, 1e-6);

        double[] chlaBiasTable = {0.051788, -0.037328, -0.037751, -0.014557, 0.007397, -0.097694, 0.010787, -0.122734};
        double chlaBias = computeUncertainty(chlaBiasTable, classificationSum, WATER_VALUES);
        assertEquals(-0.03771342, chlaBias, 1e-6);
    }

    @Ignore
    @Test
    public void testRrs_412() throws Exception {
        double classificationSum = computeClassificationSum(WATER_VALUES);

        double[] Rrs_412RmsTable = {0.001402, 0.001466, 0.001286, 0.001007, 0.001014, 0.001215, 0.001659, 0.002008};
        double Rrs_412Rms = computeUncertainty(Rrs_412RmsTable, classificationSum, WATER_VALUES);
        assertEquals(0.0010006025, Rrs_412Rms, 1e-6);

        double[] Rrs_412BiasTable = {-0.000306, 0.000096, -0.000045, 0.000083, 0.000288, 0.000078, 0.000034, -0.000653};
        double Rrs_412Bias = computeUncertainty(Rrs_412BiasTable, classificationSum, WATER_VALUES);
        assertEquals(-1.42E-004, Rrs_412Bias, 1e-6);
    }
}
