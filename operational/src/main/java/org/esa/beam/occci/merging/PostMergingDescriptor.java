package org.esa.beam.occci.merging;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.cellprocessor.FeatureSelection;
import org.esa.beam.occci.qaa.PMLv10Config;
import org.esa.beam.occci.qaa.QaaAlgorithmv6Seadas;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.qaa.SensorConfig;
import org.esa.beam.occci.qaa.binning.QaaCellProcessor;
import org.esa.beam.occci.qaa.binning.QaaConfig;
import org.esa.beam.occci.util.binning.CellProcessorParallel;

public class PostMergingDescriptor implements CellProcessorDescriptor {

    private static final String[] BAND_NAMES = new String[]{"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};
    private static final int[] ALL_IOPS = new int[]{0, 1, 2, 3, 4, 5};
    public static final String NAME = "PostMerging";

    public static class Config extends CellProcessorConfig {

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
        CellProcessor qaaCellProcessor  = createQaaProcessor(varCtx);
        CellProcessor oc4CellProcessor  = new Oc4v6CellProcessor(varCtx, Oc4v6CellProcessor.BAND_NAMES);
        CellProcessor rssIdentityProcessor = new FeatureSelection(varCtx, BAND_NAMES);
        CellProcessor sensorIdentityProcessor = new FeatureSelection(varCtx, "sensor_0", "sensor_1", "sensor_2");
        CellProcessor owtProcessor = new OWTCellProcessor(varCtx, OWTCellProcessor.BAND_NAMES);
        return new CellProcessorParallel(
                qaaCellProcessor,
                oc4CellProcessor,
                rssIdentityProcessor,
                sensorIdentityProcessor,
                owtProcessor);
    }

    public static CellProcessor create(VariableContext varCtx) {
        PostMergingDescriptor descriptor = new PostMergingDescriptor();
        CellProcessorConfig config = descriptor.createConfig();
        return descriptor.createCellProcessor(varCtx, config);
    }

    static CellProcessor createQaaProcessor(VariableContext varCtx) {
        QaaConfig qaaConfig = new QaaConfig();
        qaaConfig.setSensorName(QaaConstants.SEAWIFS);
        qaaConfig.setBandNames(BAND_NAMES);
        qaaConfig.setAtotOutIndices(ALL_IOPS);
        qaaConfig.setBbpOutIndices(ALL_IOPS);
        qaaConfig.setAphOutIndices(ALL_IOPS);
        qaaConfig.setAdgOutIndices(ALL_IOPS);

//        SensorConfig sensorConfig = SensorConfigFactory.get(QaaConstants.SEAWIFS);
        SensorConfig sensorConfig = new PMLv10Config();
        return new QaaCellProcessor(new QaaAlgorithmv6Seadas(sensorConfig), varCtx, qaaConfig);
    }
}
