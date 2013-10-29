package org.esa.beam.occci.bandshift.binning;

import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;

import java.io.IOException;

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
        String[] bandNames = bandShiftConfig.getBandNames();
        int[] outputCenterWavelengths = bandShiftConfig.getOutputCenterWavelengths();
        try {
            return new BandShiftPostProcessor(varCtx, sensorName, bandNames, outputCenterWavelengths);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to init BandShiftPostProcessor", e);
        }
    }
}
