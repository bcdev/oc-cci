package org.esa.beam.occci.merging;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.util.binning.BinningUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.esa.beam.occci.util.binning.BinningUtils.concat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class PostMergingDescriptorTest {
    private static final String[] WVLS = new String[]{"412", "443", "490", "510", "555", "670"};
    private static final String[] BAND_NAMES = {"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};
    private static final float[] INPUT = new float[]{
            0.006186951f, 0.0054932158f, 0.004319099f,
            0.0028702824f, 0.0014102075f, 0.00029511406f,
            2f, 4f, 6f};

    private CellProcessor processor;
    private Vector input;

    @Before
    public void setUp() throws Exception {
        input = new VectorImpl(INPUT);
        String[] bands = concat(BAND_NAMES, "sensor_0", "sensor_1", "sensor_2");
        VariableContext varCtx = BinningUtils.createVariableContext(bands);
        processor = PostMergingDescriptor.create(varCtx);
    }

    @Test
    public void testOutputFeatureNames() throws Exception {

        String[] iops = {"aph", "atot", "adg", "bbp"};
        String[] unc = {"bias_uncertainty", "rms_uncertainty"};
        List<String> expectOutputFeatures = new ArrayList<String>();
        List<String> allIOPs = combinationsPS(iops, WVLS);
        expectOutputFeatures.addAll(allIOPs);
        expectOutputFeatures.add("chlor_a");
        List<String> rrs = combinationsPS(new String[]{"Rrs"}, WVLS);
        expectOutputFeatures.addAll(rrs);
        expectOutputFeatures.addAll(asList("sensor_0", "sensor_1", "sensor_2"));
        expectOutputFeatures.addAll(asList("water_class1", "water_class2", "water_class3", "water_class4",
                                           "water_class5", "water_class6", "water_class7", "water_class8", "water_class9"));

        expectOutputFeatures.addAll(combinationsSP(new String[]{"chlor_a"}, unc));
        expectOutputFeatures.addAll(combinationsSP(rrs.toArray(new String[rrs.size()]), unc));

        List<String> aph = combinationsPS(new String[]{"aph"}, WVLS);
        expectOutputFeatures.addAll(combinationsSP(aph.toArray(new String[aph.size()]), unc));

        List<String> adg = combinationsPS(new String[]{"adg"}, WVLS);
        expectOutputFeatures.addAll(combinationsSP(adg.toArray(new String[adg.size()]), unc));


        String[] actualFeatures = processor.getOutputFeatureNames();
        String[] expecteds = expectOutputFeatures.toArray(new String[expectOutputFeatures.size()]);
        assertArrayEquals(expecteds, actualFeatures);

    }

    private List<String> combinationsPS(String[] prefixes, String... suffixes) {
        List<String> combinations = new ArrayList<String>(prefixes.length * suffixes.length);
        for (String prefix : prefixes) {
            for (String suffix : suffixes) {
                combinations.add(prefix + "_" + suffix);
            }
        }
        return combinations;
    }

    private List<String> combinationsSP(String[] prefixes, String... suffixes) {
        List<String> combinations = new ArrayList<String>(prefixes.length * suffixes.length);
        for (String suffix : suffixes) {
            for (String prefix : prefixes) {
                combinations.add(prefix + "_" + suffix);
            }
        }
        return combinations;
    }

    @Test
    public void testRrs() throws Exception {
        String[] names = processor.getOutputFeatureNames();
        float[] values = new float[names.length];
        processor.compute(input, new VectorImpl(values));
        assertEquals(INPUT[0], values[idxFor("Rrs_412", names)], 1e-6f);
        assertEquals(INPUT[1], values[idxFor("Rrs_443", names)], 1e-6f);
        assertEquals(INPUT[2], values[idxFor("Rrs_490", names)], 1e-6f);
        assertEquals(INPUT[3], values[idxFor("Rrs_510", names)], 1e-6f);
        assertEquals(INPUT[4], values[idxFor("Rrs_555", names)], 1e-6f);
        assertEquals(INPUT[5], values[idxFor("Rrs_670", names)], 1e-6f);
    }

    @Ignore
    @Test
    public void testRrs_Uncertainty() throws Exception {
        String[] names = processor.getOutputFeatureNames();
        float[] values = new float[names.length];
        processor.compute(input, new VectorImpl(values));
        assertEquals(0.0010006025, values[idxFor("Rrs_412_rms_uncertainty", names)], 1e-6f);
        assertEquals(8.63E-004, values[idxFor("Rrs_443_rms_uncertainty", names)], 1e-6f);
        assertEquals(0.0012822037, values[idxFor("Rrs_490_rms_uncertainty", names)], 1e-6f);
        assertEquals(0.0018298769, values[idxFor("Rrs_510_rms_uncertainty", names)], 1e-6f);
        assertEquals(0.0019287735, values[idxFor("Rrs_555_rms_uncertainty", names)], 1e-6f);
        assertEquals(0.0018553946, values[idxFor("Rrs_670_rms_uncertainty", names)], 1e-6f);

        assertEquals(-1.42E-004, values[idxFor("Rrs_412_bias_uncertainty", names)], 1e-6f);
        assertEquals(6.00E-005, values[idxFor("Rrs_443_bias_uncertainty", names)], 1e-6f);
        assertEquals(-6.89E-005, values[idxFor("Rrs_490_bias_uncertainty", names)], 1e-6f);
        assertEquals(-1.13E-005, values[idxFor("Rrs_510_bias_uncertainty", names)], 1e-6f);
        assertEquals(-2.91E-004, values[idxFor("Rrs_555_bias_uncertainty", names)], 1e-6f);
        assertEquals(3.74E-004, values[idxFor("Rrs_670_bias_uncertainty", names)], 1e-6f);
    }


    @Test
    public void testSensor() throws Exception {
        String[] names = processor.getOutputFeatureNames();
        float[] values = new float[names.length];
        processor.compute(input, new VectorImpl(values));
        assertEquals(INPUT[6], values[idxFor("sensor_0", names)], 1e-6f);
        assertEquals(INPUT[7], values[idxFor("sensor_1", names)], 1e-6f);
        assertEquals(INPUT[8], values[idxFor("sensor_2", names)], 1e-6f);
    }

    @Test
    public void testOC4v6() throws Exception {
        String[] names = processor.getOutputFeatureNames();
        float[] values = new float[names.length];
        processor.compute(input, new VectorImpl(values));
        assertEquals(0.15371986f, values[idxFor("chlor_a", names)], 1e-6f);
    }

    @Test
    public void testOC4v6_Uncertainty() throws Exception {
        String[] names = processor.getOutputFeatureNames();
        float[] values = new float[names.length];
        processor.compute(input, new VectorImpl(values));
        assertEquals(0.2661012f, values[idxFor("chlor_a_rms_uncertainty", names)], 1e-6f);
        assertEquals(-0.03771342f, values[idxFor("chlor_a_bias_uncertainty", names)], 1e-6f);
    }

    @Test
    public void testQAA() throws Exception {
        String[] names = processor.getOutputFeatureNames();
        float[] values = new float[names.length];
        processor.compute(input, new VectorImpl(values));

        assertEquals(0.009442732, values[idxFor("aph_412", names)], 1e-5f);
        assertEquals(0.012058698, values[idxFor("aph_443", names)], 1e-5f);
        assertEquals(0.008516298, values[idxFor("aph_490", names)], 1e-5f);
        assertEquals(0.005508071, values[idxFor("aph_510", names)], 1e-5f);
        assertEquals(2.25E-004, values[idxFor("aph_555", names)], 1e-5f);
        assertEquals(Float.NaN, values[idxFor("aph_670", names)], 1e-5f);

        assertEquals(0.037051506, values[idxFor("atot_412", names)], 1e-5f);
        assertEquals(0.032513037, values[idxFor("atot_443", names)], 1e-5f);
        assertEquals(0.029597668, values[idxFor("atot_490", names)], 1e-5f);
        assertEquals(0.038897656, values[idxFor("atot_510", names)], 1e-5f);
        assertEquals(0.060601648, values[idxFor("atot_555", names)], 1e-5f);
        assertEquals(0.45561144, values[idxFor("atot_670", names)], 1e-5f);

        assertEquals(0.022725413, values[idxFor("adg_412", names)], 1e-5f);
        assertEquals(0.014076872, values[idxFor("adg_443", names)], 1e-5f);
        assertEquals(0.0068099326, values[idxFor("adg_490", names)], 1e-5f);
        assertEquals(0.00499972, values[idxFor("adg_510", names)], 1e-5f);
        assertEquals(0.0024946064, values[idxFor("adg_555", names)], 1e-5f);
        assertEquals(4.22E-004, values[idxFor("adg_670", names)], 1e-5f);

        assertEquals(0.001714396, values[idxFor("bbp_412", names)], 1e-5f);
        assertEquals(0.0014909913, values[idxFor("bbp_443", names)], 1e-5f);
        assertEquals(0.0012279887, values[idxFor("bbp_490", names)], 1e-5f);
        assertEquals(0.0011369907, values[idxFor("bbp_510", names)], 1e-5f);
        assertEquals(9.66E-004, values[idxFor("bbp_555", names)], 1e-5f);
        assertEquals(6.72E-004, values[idxFor("bbp_670", names)], 1e-5f);
    }

    @Test
    public void testOWT() throws Exception {
        String[] names = processor.getOutputFeatureNames();
        float[] values = new float[names.length];
        processor.compute(input, new VectorImpl(values));

        assertEquals(5.04E-007, values[idxFor("water_class1", names)], 1e-6f);
        assertEquals(0.043338194, values[idxFor("water_class2", names)], 1e-6f);
        assertEquals(0.77421, values[idxFor("water_class3", names)], 1e-6f);
        assertEquals(4.51E-006, values[idxFor("water_class4", names)], 1e-6f);
        assertEquals(7.38E-020, values[idxFor("water_class5", names)], 1e-6f);
        assertEquals(4.31E-006, values[idxFor("water_class6", names)], 1e-6f);
        assertEquals(2.60E-004, values[idxFor("water_class7", names)], 1e-6f);
        assertEquals(1.34E-006, values[idxFor("water_class8", names)], 1e-6f);
        assertEquals(4.29E-007, values[idxFor("water_class9", names)], 1e-6f);
    }

    private static int idxFor(String bandName, String[] bandNames) {
        for (int i = 0; i < bandNames.length; i++) {
            if (bandName.equals(bandNames[i])) {
                return i;
            }
        }
        return -1;
    }

    /*
    # BEAM pin export table
    # Product:	ESACCI-OC-L3S-OC_PRODUCTS-MERGED-1D_DAILY_4km_GEO_PML_OC4v6_QAA-20060303-fv1.0
    # Wavelength:								0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0	0.0
    Name	X	Y	Lon	Lat	Color	Label	Desc	MERIS_nobs	MODISA_nobs	Rrs_412	Rrs_412_bias_uncertainty	Rrs_412_rms_uncertainty	Rrs_443	Rrs_443_bias_uncertainty	Rrs_443_rms_uncertainty	Rrs_490	Rrs_490_bias_uncertainty	Rrs_490_rms_uncertainty	Rrs_510	Rrs_510_bias_uncertainty	Rrs_510_rms_uncertainty	Rrs_555	Rrs_555_bias_uncertainty	Rrs_555_rms_uncertainty	Rrs_670	Rrs_670_bias_uncertainty	Rrs_670_rms_uncertainty	SeaWiFS_nobs	adg_412	adg_412_bias_uncertainty	adg_412_rms_uncertainty	adg_443	adg_443_bias_uncertainty	adg_443_rms_uncertainty	adg_490	adg_490_bias_uncertainty	adg_490_rms_uncertainty	adg_510	adg_510_bias_uncertainty	adg_510_rms_uncertainty	adg_555	adg_555_bias_uncertainty	adg_555_rms_uncertainty	adg_670	adg_670_bias_uncertainty	adg_670_rms_uncertainty	aph_412	aph_412_bias_uncertainty	aph_412_rms_uncertainty	aph_443	aph_443_bias_uncertainty	aph_443_rms_uncertainty	aph_490	aph_490_bias_uncertainty	aph_490_rms_uncertainty	aph_510	aph_510_bias_uncertainty	aph_510_rms_uncertainty	aph_555	aph_555_bias_uncertainty	aph_555_rms_uncertainty	aph_670	aph_670_bias_uncertainty	aph_670_rms_uncertainty	atot_412	atot_443	atot_490	atot_510	atot_555	atot_670	bbp_412	bbp_443	bbp_490	bbp_510	bbp_555	bbp_670	chlor_a	chlor_a_bias_uncertainty	chlor_a_rms_uncertainty	total_nobs	water_class1	water_class2	water_class3	water_class4	water_class5	water_class6	water_class7	water_class8	water_class9
    pin_1	2877.5	1404.5	-60.104168	31.479166	java.awt.Color[r=0,g=0,b=255]	Pin 1		17.0	6.0	0.006186951	-1.4211818E-4	0.0010006025	0.0054932158	6.004231E-5	8.6294283E-4	0.004319099	-6.885283E-5	0.0012822037	0.0028702824	-1.1293441E-5	0.0018298769	0.0014102075	-2.9079439E-4	0.0019287735	2.9511406E-4	3.7368783E-4	0.0018553946	1.0	0.022725413	0.037277605	0.07502633	0.014076872	0.011588522	0.14016885	0.0068099326	0.0052912054	0.253646	0.00499972	0.026742008	0.110795915	0.0024946064	0.079329446	0.14938283	4.220543E-4	0.01621478	0.027729819	0.009442732	0.040198624	0.09228506	0.012058698	0.13759431	0.27494285	0.008516298	0.038520865	0.1001033	0.005508071	0.006083997	0.06790722	2.2505969E-4	-0.020121653	0.09446335	NaN	0.023914974	0.031335194	0.037051506	0.032513037	0.029597668	0.038897656	0.060601648	0.45561144	0.001714396	0.0014909913	0.0012279887	0.0011369907	9.6623297E-4	6.724937E-4	0.15371986	-0.03771342	0.2661012	23.0	5.0356357E-7	0.043338194	0.77421	4.51088E-6	7.380388E-20	4.3149025E-6	2.6014398E-4	1.3443646E-6	4.2862294E-7
   */

    /*
MERIS_nobs	17
MODISA_nobs	6
Rrs_412	0,006186951
Rrs_412_bias_uncertainty	-1,42E-004
Rrs_412_rms_uncertainty	0,0010006025
Rrs_443	0,0054932158
Rrs_443_bias_uncertainty	6,00E-005
Rrs_443_rms_uncertainty	8,63E-004
Rrs_490	0,004319099
Rrs_490_bias_uncertainty	-6,89E-005
Rrs_490_rms_uncertainty	0,0012822037
Rrs_510	0,0028702824
Rrs_510_bias_uncertainty	-1,13E-005
Rrs_510_rms_uncertainty	0,0018298769
Rrs_555	0,0014102075
Rrs_555_bias_uncertainty	-2,91E-004
Rrs_555_rms_uncertainty	0,0019287735
Rrs_670	2,95E-004
Rrs_670_bias_uncertainty	3,74E-004
Rrs_670_rms_uncertainty	0,0018553946
SeaWiFS_nobs	1
adg_412	0,022725413
adg_412_bias_uncertainty	0,037277605
adg_412_rms_uncertainty	0,07502633
adg_443	0,014076872
adg_443_bias_uncertainty	0,011588522
adg_443_rms_uncertainty	0,14016885
adg_490	0,0068099326
adg_490_bias_uncertainty	0,0052912054
adg_490_rms_uncertainty	0,253646
adg_510	0,00499972
adg_510_bias_uncertainty	0,026742008
adg_510_rms_uncertainty	0,110795915
adg_555	0,0024946064
adg_555_bias_uncertainty	0,079329446
adg_555_rms_uncertainty	0,14938283
adg_670	4,22E-004
adg_670_bias_uncertainty	0,01621478
adg_670_rms_uncertainty	0,027729819
aph_412	0,009442732
aph_412_bias_uncertainty	0,040198624
aph_412_rms_uncertainty	0,09228506
aph_443	0,012058698
aph_443_bias_uncertainty	0,13759431
aph_443_rms_uncertainty	0,27494285
aph_490	0,008516298
aph_490_bias_uncertainty	0,038520865
aph_490_rms_uncertainty	0,1001033
aph_510	0,005508071
aph_510_bias_uncertainty	0,006083997
aph_510_rms_uncertainty	0,06790722
aph_555	2,25E-004
aph_555_bias_uncertainty	-0,020121653
aph_555_rms_uncertainty	0,09446335
aph_670	NaN
aph_670_bias_uncertainty	0,023914974
aph_670_rms_uncertainty	0,031335194
atot_412	0,037051506
atot_443	0,032513037
atot_490	0,029597668
atot_510	0,038897656
atot_555	0,060601648
atot_670	0,45561144
bbp_412	0,001714396
bbp_443	0,0014909913
bbp_490	0,0012279887
bbp_510	0,0011369907
bbp_555	9,66E-004
bbp_670	6,72E-004
chlor_a	0,15371986
chlor_a_bias_uncertainty	-0,03771342
chlor_a_rms_uncertainty	0,2661012
total_nobs	23
water_class1	5,04E-007
water_class2	0,043338194
water_class3	0,77421
water_class4	4,51E-006
water_class5	7,38E-020
water_class6	4,31E-006
water_class7	2,60E-004
water_class8	1,34E-006
water_class9	4,29E-007

     */
}
