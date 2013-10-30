package org.esa.beam.occci.merging;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.coastcolour.fuzzy.Auxdata;
import org.esa.beam.coastcolour.fuzzy.FuzzyClassification;
import org.esa.beam.occci.util.binning.BinningUtils;

import java.net.URL;
import java.util.ArrayList;

public class OWTCellProcessor extends CellProcessor {

    //static final String[] BAND_NAMES = new String[]{"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};
    static final String[] BAND_NAMES = new String[]{"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555"};

    //private static final String AUXDATA_PATH = "owt16_meris_stats_101119_5band.hdf";
    private static final String AUXDATA_PATH = "owt16_seawifs_stats_101111.hdf";

    private final int[] rrsBandIndices;
    private final FuzzyClassification fuzzyClassification;

    public OWTCellProcessor(VariableContext varCtx, String[] bandNames) {
        super(createOutputFeatureNames());
        rrsBandIndices = BinningUtils.getBandIndices(varCtx, bandNames);

        final URL resourceUrl = FuzzyClassification.class.getResource(AUXDATA_PATH);
        try {
            final Auxdata auxdata = new Auxdata(resourceUrl.toURI());
            fuzzyClassification = new FuzzyClassification(auxdata.getSpectralMeans(),
                                                          auxdata.getInvertedCovarianceMatrices());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load auxdata", e);
        }
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        double[] rrsBelowWater = new double[rrsBandIndices.length];
        for (int i = 0; i < rrsBelowWater.length; i++) {
            rrsBelowWater[i] = convertToSubsurfaceWaterRrs(inputVector.get(rrsBandIndices[i]));
        }

        double[] membershipIndicators = fuzzyClassification.computeClassMemberships(rrsBelowWater);
        // setting the values for the first 8 classes
        double ninthClassValue = 0.0;
        for (int i = 0; i < 8; i++) {
            double membershipIndicator = membershipIndicators[i];
            outputVector.set(i, (float) membershipIndicator);
            ninthClassValue += membershipIndicator;
        }
        // setting the value for the 9th class to the sum of the last 8 classes
        outputVector.set(8, (float) ninthClassValue);
    }

    // TODO is this right ?
    private static double convertToSubsurfaceWaterRrs(double merisL2Reflec) {
        // convert to remote sensing reflectances
        final double rrsAboveWater = merisL2Reflec / Math.PI;
        // convert to subsurface water remote sensing reflectances
        return rrsAboveWater / (0.52 + 1.7 * rrsAboveWater);
    }

    private static String[] createOutputFeatureNames() {
        final ArrayList<String> featureNameList = new ArrayList<String>();
        for (int i = 1; i <= 9; i++) {
            featureNameList.add("water_class" + i);
        }
        return featureNameList.toArray(new String[featureNameList.size()]);
    }

}
