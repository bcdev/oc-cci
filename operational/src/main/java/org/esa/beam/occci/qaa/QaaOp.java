package org.esa.beam.occci.qaa;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.jai.ResolutionLevel;
import org.esa.beam.jai.VirtualBandOpImage;

import java.awt.Rectangle;

@SuppressWarnings({"UnusedDeclaration"})
@OperatorMetadata(alias = "OC-CCI.QaaIOP",
                  description = "Performs retrieval of inherent optical properties (IOPs) for " +
                          "coastal and open ocean waters for different sensors. (for oc-cci)",
                  authors = " Zhongping Lee, Mingrui Zhang (WSU); Marco Peters, Tom Block, Marco Zuehlke (Brockmann Consult)",
                  copyright = "(C) 2011 by NRL and WSU",
                  version = "2.0")
public class QaaOp extends PixelOperator {

    private static final String PRODUCT_TYPE = "QAA_L2";

    private static final String A_TOTAL_PATTERN = "a_total_%d";
    private static final String BB_SPM_PATTERN = "bb_spm_%d";
    private static final String A_PIG_PATTERN = "a_pig_%d";
    private static final String A_YS_PATTERN = "a_ys_%d";


    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @Parameter(description = "The names of the reflectance bands.", notNull = true)
    private String[] reflectanceBandNames;

    @Parameter(description = "The name of the sensor",
               defaultValue = QaaConstants.MERIS,
               valueSet = {QaaConstants.MERIS, QaaConstants.MODIS, QaaConstants.SEAWIFS})
    private String sensorName;

    @Parameter(defaultValue = "l2_flags.WATER",
               description = "Expression defining pixels considered for processing.")
    private String validPixelExpression;

    @Parameter(defaultValue = "true", label = "Divide source Rrs by PI(3.14)",
               description = "If selected the source remote reflectances are divided by PI")
    private boolean divideByPI;

    private VirtualBandOpImage validOpImage;
    private QaaAlgorithm qaaAlgorithm;
    private ThreadLocal<QaaResult> qaaResult;
    private SensorConfig sensorConfig;

    @Override
    protected void prepareInputs() throws OperatorException {
        validateSourceProduct();
        if (!sourceProduct.isCompatibleBandArithmeticExpression(validPixelExpression)) {
            String message = String.format("The given expression '%s' is not compatible with the source product.",
                                           validPixelExpression);
            throw new OperatorException(message);
        }
        validOpImage = VirtualBandOpImage.createMask(validPixelExpression,
                                                     sourceProduct,
                                                     ResolutionLevel.MAXRES);

        sensorConfig = SensorConfigFactory.get(sensorName);
        qaaAlgorithm = new QaaAlgorithm(sensorConfig);

        qaaResult = new ThreadLocal<QaaResult>() {
            @Override
            protected QaaResult initialValue() {
                return new QaaResult();
            }
        };
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer configurer) {
        super.configureTargetProduct(configurer);
        double[] wavelengths = sensorConfig.getWavelengths();
        for (int i = 0; i < QaaConstants.NUM_A_TOTAL_BANDS; i++) {
            addBand(configurer, A_TOTAL_PATTERN, wavelengths[i],
                    "Total absorption coefficient of all water constituents at %d nm.");
        }
        for (int i = 0; i < QaaConstants.NUM_BB_SPM_BANDS; i++) {
            addBand(configurer, BB_SPM_PATTERN, wavelengths[i],
                    "Backscattering of suspended particulate matter at %d nm.");
        }

        for (int i = 0; i < QaaConstants.NUM_A_PIG_BANDS; i++) {
            addBand(configurer, A_PIG_PATTERN, wavelengths[i],
                    "Pigment absorption coefficient at %d nm.");
        }

        for (int i = 0; i < QaaConstants.NUM_A_YS_BANDS; i++) {
            addBand(configurer, A_YS_PATTERN, wavelengths[i],
                    "Yellow substance absorption coefficient at %d nm.");
        }

        Product targetProduct = configurer.getTargetProduct();
        final int sceneWidth = targetProduct.getSceneRasterWidth();
        final int sceneHeight = targetProduct.getSceneRasterHeight();

        targetProduct.setProductType(PRODUCT_TYPE);
    }


    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        for (int i = 0; i < 6; i++) {
            sampleConfigurer.defineSample(i, reflectanceBandNames[i]);
        }
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        final String[] targetBandNames = getTargetProduct().getBandNames();
        int sampleIndex = 0;
        for (final String targetBandName : targetBandNames) {
            if (!getTargetProduct().getBand(targetBandName).isSourceImageSet()) {
                sampleConfigurer.defineSample(sampleIndex++, targetBandName);
            }
        }
    }

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        QaaResult result = qaaResult.get();

        if (isSampleValid(x, y)) { // Check if it is water
            final float[] rrs = new float[sourceSamples.length];
            for (int i = 0; i < rrs.length; i++) {
                rrs[i] = sourceSamples[i].getFloat();
            }
            try {
                result = qaaAlgorithm.process(rrs, result);
            } catch (ImaginaryNumberException ine) {
                result.invalidate();
            }
        } else {
            result.invalidate();
        }

        writeResult(targetSamples, result);
    }

    static void writeResult(WritableSample[] targetSamples, QaaResult qaaResult) {
        int sampleIndex = 0;
        for (float anA_total : qaaResult.getA_Total()) {
            targetSamples[sampleIndex++].set(anA_total);
        }

        for (float aBb_spm : qaaResult.getBB_SPM()) {
            targetSamples[sampleIndex++].set(aBb_spm);
        }

        for (float anA_pig : qaaResult.getA_PIG()) {
            targetSamples[sampleIndex++].set(anA_pig);
        }

        for (float a_y : qaaResult.getA_YS()) {
            targetSamples[sampleIndex++].set(a_y);
        }
    }

    private void validateSourceProduct() {
        for (String bandName : reflectanceBandNames) {
            if (!sourceProduct.containsBand(bandName)) {
                String msg = String.format("Source product must contain a band with the name '%s'", bandName);
                throw new OperatorException(msg);
            }
        }
    }

    private boolean isSampleValid(int x, int y) {
        return validOpImage.getData(new Rectangle(x, y, 1, 1)).getSample(x, y, 0) != 0;
    }

    private Band addBand(ProductConfigurer configurer, String namePattern, double wavelength, String descriptionPattern) {
        String bandName = String.format(namePattern, (int) wavelength);
        Band band = configurer.addBand(bandName, ProductData.TYPE_FLOAT32, QaaConstants.NO_DATA_VALUE);
        band.setDescription(String.format(descriptionPattern, (int) wavelength));
        band.setUnit("m^-1");
        band.setSpectralWavelength((float) wavelength);
        band.setNoDataValueUsed(true);
        return band;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(QaaOp.class);
        }
    }
}
