package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.support.VariableContextImpl;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.occci.bandshift.Sensor;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.qaa.binning.QaaConfig;
import org.esa.beam.occci.qaa.binning.QaaDescriptor;
import org.esa.beam.occci.util.binning.ChainProcessor;
import org.esa.beam.occci.util.binning.MarkSensorProcessor;
import org.esa.beam.occci.util.binning.SumToMeanCellProcessor;

/**
 * Combines all steps for SeaWifs band shifting.
 */
public class BandShiftChainDescriptor implements CellProcessorDescriptor {

    public static final String NAME = "BandShiftChain";
    private static final int[] BS_OUTPUT_CENTER_WAVELENGTHS = {412, 443, 490, 510, 555, 670};

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

            String[] bsInputFeatures = {"Rrs412_mean", "Rrs443_mean", "Rrs490_mean", "Rrs510_mean", "Rrs560_mean", "Rrs665_mean", "a_pig_443_mean", "a_ys_443_mean", "bb_spm_443_mean"};

            BandShiftDescriptor bandShiftDescriptor = new BandShiftDescriptor();
            BandShiftConfig bandShiftConfig = (BandShiftConfig) bandShiftDescriptor.createConfig();
            bandShiftConfig.setSensorName(Sensor.MERIS.name());
            bandShiftConfig.setBandNames(bsInputFeatures);
            bandShiftConfig.setOutputCenterWavelengths(BS_OUTPUT_CENTER_WAVELENGTHS);
            CellProcessor bandshiftProcessor = bandShiftDescriptor.createCellProcessor(varCtx, bandShiftConfig);

            VariableContext variableContext = createVariableContext(bandshiftProcessor.getOutputFeatureNames());
            CellProcessor markProcessor = new MarkSensorProcessor(variableContext, 0);

            return new ChainProcessor(bandshiftProcessor, markProcessor);
        } else if (QaaConstants.MODIS.equals(config.sensorName)) {

            String[] sumFeatureNames = {"Rrs_412_sum", "Rrs_443_sum", "Rrs_488_sum", "Rrs_531_sum", "Rrs_547_sum", "Rrs_667_sum"};
            String weightFeatureName = "weights";
            CellProcessor sumToMeanProcessor = new SumToMeanCellProcessor(varCtx, weightFeatureName, sumFeatureNames);

            VariableContext qaaVarCtx = createVariableContext(sumToMeanProcessor.getOutputFeatureNames());
            String[] qaaInputFeatures = {"Rrs_412", "Rrs_443", "Rrs_488", "Rrs_531", "Rrs_547", "Rrs_667"};
            QaaDescriptor qaaDescriptor = new QaaDescriptor();
            QaaConfig qaaConfig = (QaaConfig) qaaDescriptor.createConfig();
            qaaConfig.setSensorName(QaaConstants.MODIS);
            qaaConfig.setBandNames(qaaInputFeatures);
            qaaConfig.setATotalOutIndices(new int[0]);
            qaaConfig.setBbSpmOutIndices(new int[]{1});
            qaaConfig.setAPigOutIndices(new int[]{1});
            qaaConfig.setAYsOutIndices(new int[]{1});
            qaaConfig.setRrsOut(true);
            CellProcessor qaaProcessor = qaaDescriptor.createCellProcessor(qaaVarCtx, qaaConfig);

            VariableContext bandShiftVarCtx = createVariableContext(qaaProcessor.getOutputFeatureNames());
            String[] bsInputFeatures = {"Rrs_412", "Rrs_443", "Rrs_488", "Rrs_531", "Rrs_547", "Rrs_667", "a_pig_443", "a_ys_443", "bb_spm_443"};

            BandShiftDescriptor bandShiftDescriptor = new BandShiftDescriptor();
            BandShiftConfig bandShiftConfig = (BandShiftConfig) bandShiftDescriptor.createConfig();
            bandShiftConfig.setSensorName(Sensor.MODISA.name());
            bandShiftConfig.setBandNames(bsInputFeatures);
            bandShiftConfig.setOutputCenterWavelengths(BS_OUTPUT_CENTER_WAVELENGTHS);
            CellProcessor bandshiftProcessor = bandShiftDescriptor.createCellProcessor(bandShiftVarCtx, bandShiftConfig);

            VariableContext markVarCtx = createVariableContext(bandshiftProcessor.getOutputFeatureNames());
            CellProcessor markProcessor = new MarkSensorProcessor(markVarCtx, 1);

            return new ChainProcessor(sumToMeanProcessor, qaaProcessor, bandshiftProcessor, markProcessor);
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
