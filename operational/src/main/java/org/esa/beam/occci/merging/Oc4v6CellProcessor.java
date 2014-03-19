package org.esa.beam.occci.merging;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.occci.util.binning.BinningUtils;

public class Oc4v6CellProcessor extends CellProcessor {

    static final String CHLOR_A = "chlor_a";
    static final String[] BAND_NAMES = new String[]{"Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555"};

    private final int[] rrsBandIndices;

    public Oc4v6CellProcessor(VariableContext varCtx, String[] bandNames) {
        super(new String[]{CHLOR_A});
        rrsBandIndices = BinningUtils.getBandIndices(varCtx, bandNames);
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {

        final double[] chloc4_coef = {0.3272f, -2.9940f, 2.7218f, -1.2259f, -0.5683f};
        final double minrat = 0.21f;
        final double maxrat = 30.0f;
        final double chlmin = 0.0;
        final double chlmax = 1000.0;

        double rat, minRrs;
        double chl = Double.NaN;

        double rrs1 = inputVector.get(rrsBandIndices[0]);
        double rrs2 = inputVector.get(rrsBandIndices[1]);
        double rrs3 = inputVector.get(rrsBandIndices[2]);
        double rrs4 = inputVector.get(rrsBandIndices[3]);

        minRrs = Math.min(rrs1, rrs2);

        if (rrs4 > 0.0 && rrs3 > 0.0 && (rrs2 > 0.0 || rrs1 * rrs2 > 0.0) && minRrs > -0.001) {
            rat = Math.max(Math.max(rrs1, rrs2), rrs3) / rrs4;
            if (rat > minrat && rat < maxrat) {
                rat = Math.log10(rat);
                chl = Math.pow(10.0, (chloc4_coef[0] + rat * (chloc4_coef[1] + rat * (chloc4_coef[2] + rat * (chloc4_coef[3] + rat * chloc4_coef[4])))));
                chl = (chl > chlmin ? chl : chlmin);
                chl = (chl < chlmax ? chl : chlmax);
            }
        }
        outputVector.set(0, (float) chl);
    }
}
