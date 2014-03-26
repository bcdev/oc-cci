package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.binning.cellprocessor.FeatureSelection;
import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.occci.bandshift.Sensor;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.qaa.binning.QaaCellProcessor;
import org.esa.beam.occci.qaa.binning.QaaConfig;
import org.esa.beam.occci.util.binning.BinningUtils;
import org.esa.beam.occci.util.binning.CellProcessorParallel;
import org.esa.beam.occci.util.binning.CellProcessorSequence;
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

        public String getSensorName() {
            return sensorName;
        }

        public void setSensorName(String sensorName) {
            this.sensorName = sensorName;
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

            String[] rrsInputFeatures = {"Rrs412_mean", "Rrs443_mean", "Rrs490_mean", "Rrs510_mean", "Rrs560_mean", "Rrs665_mean"};

            QaaConfig qaaConfig = new QaaConfig();
            qaaConfig.setSensorName(QaaConstants.MERIS);
            qaaConfig.setBandNames(rrsInputFeatures);
            qaaConfig.setAtotOutIndices(new int[0]);
            qaaConfig.setBbpOutIndices(new int[]{1});
            qaaConfig.setAphOutIndices(new int[]{1});
            qaaConfig.setAdgOutIndices(new int[]{1});
            CellProcessor qaaProcessor = new QaaCellProcessor(varCtx, qaaConfig);
            String[] iopFeatureNames = qaaProcessor.getOutputFeatureNames();

            CellProcessor rrsPassThrough = new FeatureSelection(varCtx, rrsInputFeatures);
            CellProcessor qaaPlusRrs = new CellProcessorParallel(rrsPassThrough, qaaProcessor);
            VariableContext qaaPlusRrsVarCtx = BinningUtils.createVariableContext(qaaPlusRrs.getOutputFeatureNames());

            CellProcessor bandshiftProcessor =new BandShiftCellProcessor(qaaPlusRrsVarCtx,
                                                                         Sensor.MERIS_NAME,
                                                                         rrsInputFeatures,
                                                                         iopFeatureNames,
                                                                         BS_OUTPUT_CENTER_WAVELENGTHS);

            CellProcessor bandshiftProcessorSequence = new CellProcessorSequence(qaaPlusRrs, bandshiftProcessor);
            CellProcessor markProcessor = new MarkSensorProcessor(0);
            return new CellProcessorParallel(bandshiftProcessorSequence, markProcessor);
        } else if (QaaConstants.MODIS.equals(config.sensorName)) {

            String[] modisSumFeatures = {"Rrs_412_sum", "Rrs_443_sum", "Rrs_488_sum", "Rrs_531_sum", "Rrs_547_sum", "Rrs_667_sum"};
            String weightFeatureName = "weights";
            String[] modisRrsFeatures = {"Rrs_412", "Rrs_443", "Rrs_488", "Rrs_531", "Rrs_547", "Rrs_667"};

            CellProcessor sumToMeanProcessor = new SumToMeanCellProcessor(varCtx, weightFeatureName, modisSumFeatures);

            String[] meanOutFeatures = sumToMeanProcessor.getOutputFeatureNames();
            VariableContext qaaVarCtx = BinningUtils.createVariableContext(meanOutFeatures);
            QaaConfig qaaConfig = new QaaConfig();
            qaaConfig.setSensorName(QaaConstants.MODIS);
            qaaConfig.setBandNames(modisRrsFeatures);
            qaaConfig.setAtotOutIndices(new int[0]);
            qaaConfig.setBbpOutIndices(new int[]{1});
            qaaConfig.setAphOutIndices(new int[]{1});
            qaaConfig.setAdgOutIndices(new int[]{1});
            CellProcessor qaaProcessor = new QaaCellProcessor(qaaVarCtx, qaaConfig);

            String[] qaaOutFeatures = qaaProcessor.getOutputFeatureNames();
            String[] meanPlusQaaFeatures = BinningUtils.concat(meanOutFeatures, qaaOutFeatures);
            VariableContext bandShiftVarCtx = BinningUtils.createVariableContext(meanPlusQaaFeatures);
            CellProcessor bandshiftProcessor = new BandShiftCellProcessor(bandShiftVarCtx,
                                                                          Sensor.MODISA_NAME,
                                                                          modisRrsFeatures,
                                                                          qaaOutFeatures,
                                                                          BS_OUTPUT_CENTER_WAVELENGTHS);

            CellProcessor markProcessor = new MarkSensorProcessor(1);
            CellProcessor modisBandShiftProcessor = new ModisBandShiftProcessor(sumToMeanProcessor, qaaProcessor, bandshiftProcessor);
            return new CellProcessorParallel(modisBandShiftProcessor, markProcessor);
        } else if (QaaConstants.SEAWIFS.equals(config.sensorName)) {

            String[] sumFeatureNames = {"Rrs_412_sum", "Rrs_443_sum", "Rrs_490_sum", "Rrs_510_sum", "Rrs_555_sum", "Rrs_670_sum"};
            String weightFeatureName = "weights";
            CellProcessor sumToMeanProcessor = new SumToMeanCellProcessor(varCtx, weightFeatureName, sumFeatureNames);

            CellProcessor markProcessor = new MarkSensorProcessor(2);
            return new CellProcessorParallel(sumToMeanProcessor, markProcessor);
        } else {
            throw new IllegalArgumentException("Unsupported sensor: " + config.sensorName);
        }
    }

    private static class ModisBandShiftProcessor extends CellProcessor {

        private final CellProcessor sumToMeanProcessor;
        private final CellProcessor qaaProcessor;
        private final CellProcessor bandshiftProcessor;
        private final WritableVector meanVector;
        private final WritableVector qaaVector;
        private final Vector meanPlusQaaVector;

        protected ModisBandShiftProcessor(CellProcessor sumToMeanProcessor, CellProcessor qaaProcessor, CellProcessor bandshiftProcessor) {
            super(bandshiftProcessor.getOutputFeatureNames());
            this.sumToMeanProcessor = sumToMeanProcessor;
            this.qaaProcessor = qaaProcessor;
            this.bandshiftProcessor = bandshiftProcessor;
            int numMeanRrs = sumToMeanProcessor.getOutputFeatureNames().length;
            int numQaa = qaaProcessor.getOutputFeatureNames().length;
            float[] meanPlusQaaElems = new float[numMeanRrs + numQaa];

            meanVector = new VectorImpl(meanPlusQaaElems);
            ((VectorImpl)meanVector).setOffsetAndSize(0, numMeanRrs);

            qaaVector = new VectorImpl(meanPlusQaaElems);
            ((VectorImpl)qaaVector).setOffsetAndSize(numMeanRrs, numQaa);

            meanPlusQaaVector = new VectorImpl(meanPlusQaaElems);
        }

        @Override
        public void compute(Vector inputVector, WritableVector outputVector) {
            sumToMeanProcessor.compute(inputVector, meanVector);
            qaaProcessor.compute(meanVector, qaaVector);
            bandshiftProcessor.compute(meanPlusQaaVector, outputVector);
        }

    }

}
