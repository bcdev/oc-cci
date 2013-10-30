package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;


public class BandShiftDescriptor implements CellProcessorDescriptor {

    public static final String BAND_SHIFTING = "BandShifting";

    public String getName() {
        return BAND_SHIFTING;
    }

    @Override
    public CellProcessorConfig createConfig() {
        return new BandShiftConfig(BAND_SHIFTING);
    }

    @Override
    public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig config) {
        final BandShiftConfig bandShiftConfig = (BandShiftConfig) config;

        String sensorName = bandShiftConfig.getSensorName();
        String[] rssBandNames = bandShiftConfig.getRrsBandNames();
        String[] iopBandNames = bandShiftConfig.getIopBandNames();
        int[] outputCenterWavelengths = bandShiftConfig.getOutputCenterWavelengths();
        return new BandShiftCellProcessor(varCtx, sensorName, rssBandNames, iopBandNames, outputCenterWavelengths);
    }
}
