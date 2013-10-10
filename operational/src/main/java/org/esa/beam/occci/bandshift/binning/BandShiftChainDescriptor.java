package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.support.VariableContextImpl;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.occci.bandshift.Sensor;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.util.binning.ChainProcessor;
import org.esa.beam.occci.util.binning.MarkSensorProcessor;
import org.esa.beam.occci.util.binning.SumToMeanCellProcessor;

/**
 * Combines all steps for SeaWifs band shifting.
 */
public class BandShiftChainDescriptor implements CellProcessorDescriptor {

    public static final String NAME = "BandShiftChain";

    public static class Config extends CellProcessorConfig {
        @Parameter(notNull = true,
                   notEmpty = true,
                   valueSet = {QaaConstants.MERIS, QaaConstants.MODIS, QaaConstants.SEAWIFS})
        private String sensorName;

        public Config() {
            super(NAME);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CellProcessorConfig createConfig() {
        return new Config();
    }

    @Override
    public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig cellProcessorConfig) {
        Config config = (Config) cellProcessorConfig;

        if (QaaConstants.MERIS.equals(config.sensorName)) {

            String[] bandNames = {"Rrs412_mean", "Rrs443_mean", "Rrs490_mean", "Rrs510_mean", "Rrs560_mean", "Rrs665_mean", "a_pig_413_mean", "a_ys_413_mean", "bb_spm_413_mean"};
            int[] outputCenterWavelengths = {412, 443, 490, 510, 555, 670};

            BandShiftDescriptor bandShiftDescriptor = new BandShiftDescriptor();
            BandShiftConfig bandShiftConfig = (BandShiftConfig) bandShiftDescriptor.createConfig();
            bandShiftConfig.setSensorName(Sensor.MERIS.name());
            bandShiftConfig.setBandNames(bandNames);
            bandShiftConfig.setOutputCenterWavelengths(outputCenterWavelengths);
            CellProcessor bandshiftProcessor = bandShiftDescriptor.createCellProcessor(varCtx, bandShiftConfig);

            VariableContext variableContext = createVariableContext(bandshiftProcessor.getOutputFeatureNames());
            CellProcessor markProcessor = new MarkSensorProcessor(variableContext, 0);

            return new ChainProcessor(bandshiftProcessor, markProcessor);
        } else if (QaaConstants.MODIS.equals(config.sensorName)) {
            return null;
        } else if (QaaConstants.SEAWIFS.equals(config.sensorName)) {

            String[] sumFeatureNames = {"Rrs_412_sum", "Rrs_443_sum", "Rrs_490_sum", "Rrs_510_sum", "Rrs_555_sum", "Rrs_670_sum"};
            String weightFeatureName = "weights";
            CellProcessor sumToMeanProcessor = new SumToMeanCellProcessor(varCtx, weightFeatureName, sumFeatureNames);

            VariableContext variableContext = createVariableContext(sumToMeanProcessor.getOutputFeatureNames());
            CellProcessor markProcessor = new MarkSensorProcessor(variableContext, 2);

            return new ChainProcessor(sumToMeanProcessor, markProcessor);
        } else {
            throw new IllegalArgumentException("Unsupported sensor: " + config.sensorName);
        }
    }

    private static VariableContext createVariableContext(String... outputFeatureNames) {
        VariableContextImpl variableContext = new VariableContextImpl();
        for (String outputFeatureName : outputFeatureNames) {
            variableContext.defineVariable(outputFeatureName);
        }
        return variableContext;
    }
}
