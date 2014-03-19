package org.esa.beam.occci.merging;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.coastcolour.fuzzy.FuzzyClassification;
import org.esa.beam.occci.util.binning.BinningUtils;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class OWTCellProcessor extends CellProcessor {

    static final String[] BAND_NAMES = new String[]{"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};
    static final String WATER_CLASS_PREFIX = "water_class";

    private static final int NUM_BANDS = 6;
    private static final int NUM_CLASSES = 16;

    // number of optical water types
    private static final int NUM_OWTS = 9;
    private static final String MEAN_FILE = "seawifs_means.dat";
    private static final String COV_FILE = "seawifs_covariance.dat";

    private final int[] rrsBandIndices;
    private final FuzzyClassification fuzzyClassification;

    public OWTCellProcessor(VariableContext varCtx, String[] bandNames) {
        super(createWaterClassFeatureNames());
        rrsBandIndices = BinningUtils.getBandIndices(varCtx, bandNames);

        try {
            double[][] reflectanceMeans = readReflectanceMeans();
            double[][][] invertedClassCovMatrix = readInvertedClassCovMatrix();
            fuzzyClassification = new FuzzyClassification(reflectanceMeans, invertedClassCovMatrix);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load auxdata", e);
        }
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        double[] rrsBelowWater = new double[rrsBandIndices.length];
        int negativCount = 0;
        for (int i = 0; i < rrsBelowWater.length; i++) {
            float aboveWater = inputVector.get(rrsBandIndices[i]);
            if (aboveWater < 0) {
                negativCount++;
            }
            if (Float.isNaN(aboveWater)) {
                BinningUtils.setToInvalid(outputVector);
                return;
            }
            rrsBelowWater[i] = convertToSubsurfaceWaterRrs(aboveWater);
        }
        if (negativCount == rrsBandIndices.length) {
            BinningUtils.setToInvalid(outputVector);
            return;
        }

        double[] membershipIndicators = fuzzyClassification.computeClassMemberships(rrsBelowWater);

        // setting the values for the first 8 classes
        for (int i = 0; i < NUM_OWTS - 1; i++) {
            double membershipIndicator = membershipIndicators[i];
            outputVector.set(i, (float) membershipIndicator);
        }

        // setting the value for the 9th class to the sum of the last 8 classes
        double ninthClassValue = 0.0;
        for (int i = NUM_OWTS - 1; i < membershipIndicators.length; i++) {
            ninthClassValue += membershipIndicators[i];
        }
        outputVector.set(NUM_OWTS - 1, (float) ninthClassValue);
    }

    /**
     * convert from Rrs (above water reflectance) to rrs (sub-surface reflectance)
     */
    private static double convertToSubsurfaceWaterRrs(double aboveWater) {
        return aboveWater / (0.52 + 1.7 * aboveWater);
    }

    static String[] createWaterClassFeatureNames() {
        final ArrayList<String> featureNameList = new ArrayList<String>();
        for (int i = 1; i <= NUM_OWTS; i++) {
            featureNameList.add(WATER_CLASS_PREFIX + i);
        }
        return featureNameList.toArray(new String[featureNameList.size()]);
    }

    static double[][] readReflectanceMeans() throws IOException {
        return readReflectanceMeans(MEAN_FILE, NUM_BANDS, NUM_CLASSES);
    }

    static double[][][] readInvertedClassCovMatrix() throws IOException {
        return readInvertedClassCovMatrix(COV_FILE, NUM_BANDS, NUM_CLASSES);
    }

    /**
     * A two dimensional array specifying the mean spectrum for each class.
     * The first dimension specifies the number of bands, the second specifies the number of classes.
     */
    static double[][] readReflectanceMeans(String filename, int numBands, int numClasses) throws IOException {
        InputStream stream = OWTCellProcessor.class.getResourceAsStream(filename);
        MemoryCacheImageInputStream imageInputStream = new MemoryCacheImageInputStream(stream);
        imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        try {
            double[][] values = new double[numBands][numClasses];
            for (int bandIndex = 0; bandIndex < numBands; bandIndex++) {
                for (int classIndex = 0; classIndex < numClasses; classIndex++) {
                    values[bandIndex][classIndex] = imageInputStream.readDouble();
                }
            }
            return values;
        } finally {
            imageInputStream.close();
        }
    }

    /**
     * A three dimensional array.
     * The first dimension specifies the number of classes,
     * the second and third dimensions build up the squared matrix defined by
     * the number of wavelength.
     */
    static double[][][] readInvertedClassCovMatrix(String filename, int numBands, int numClasses) throws IOException {
        InputStream stream = OWTCellProcessor.class.getResourceAsStream(filename);
        MemoryCacheImageInputStream imageInputStream = new MemoryCacheImageInputStream(stream);
        imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        try {
            double[][][] values = new double[numClasses][numBands][numBands];
            for (int i1 = 0; i1 < numClasses; i1++) {
                for (int i2 = 0; i2 < numClasses; i2++) {
                    for (int i3 = 0; i3 < numClasses; i3++) {
                        // the binary file has 3 equally sized dimensions
                        double v = imageInputStream.readDouble();
                        if (i2 < numBands && i3 < numBands) {
                            values[i1][i2][i3] = v;
                        }
                    }
                }
            }
            return values;
        } finally {
            imageInputStream.close();
        }
    }

    public static void main(String[] args) throws IOException {
        double[][] reflectanceMeans = readReflectanceMeans();
        System.out.println("reflectanceMeans = " + Arrays.deepToString(reflectanceMeans).replaceAll("\\],", "\\],\n"));

        System.out.println();

        double[][][] invertedClassCovMatrix = readInvertedClassCovMatrix();
        System.out.println("invertedClassCovMatrix = " + Arrays.deepToString(invertedClassCovMatrix).replaceAll("\\],", "\\],\n"));

    }
}
