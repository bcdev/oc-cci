package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.occci.qaa.QaaConstants;

public class QaaDescriptor implements CellProcessorDescriptor {

    @Override
    public String getName() {
        return "QAA";
    }

    @Override
    public CellProcessorConfig createConfig() {
        return new QaaConfig();
    }

    @Override
    public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig postProcessorConfig) {
        final QaaConfig config = (QaaConfig) postProcessorConfig;
        validate(config);

        return new QaaCellProcessor(varCtx, config);
    }

    // package access for testing only tb 2013-04-24
    static int getWavelengthInt(double[] wavelengths, int index) {
        if (index < 0 || index >= wavelengths.length) {
            throw new IllegalArgumentException("Illegal wavelength index: " + index);
        }

        final double wavelength = wavelengths[index];
        return (int) Math.round(wavelength);
    }

    // package access for testing only tb 2013-04-24
    static void validate(QaaConfig config) {
        final String sensorName = config.getSensorName();
        if (!(QaaConstants.MERIS.equals(sensorName) ||
                QaaConstants.MODIS.equals(sensorName) ||
                QaaConstants.SEAWIFS.equals(sensorName))) {
            throw new IllegalArgumentException("Invalid sensor: " + sensorName);
        }

        final String[] bandNames = config.getBandNames();
        if (bandNames.length != 6) {
            throw new IllegalArgumentException("Illegal number of input bands: must provide six reflectance band names");
        }

        final int[] aphOutIndices = config.getAphOutIndices();
        if (aphOutIndices.length > 3) {
            throw new IllegalArgumentException("Invalid number of aph_out indices: " + aphOutIndices.length + ". Must be in [0, 3]");
        }
        for (int index : aphOutIndices) {
            if (index < 0 || index > 2) {
                throw new IllegalArgumentException("Invalid aph_out index: " + index + ". Must be in [0, 2]");
            }
        }

        final int[] atotOutIndices = config.getAtotOutIndices();
        if (atotOutIndices.length > 5) {
            throw new IllegalArgumentException("Invalid number of atot_out indices: " + atotOutIndices.length + ". Must be in [0, 5]");
        }
        for (int index : atotOutIndices) {
            if (index < 0 || index > 4) {
                throw new IllegalArgumentException("Invalid atot_out index: " + index + ". Must be in [0, 4]");
            }
        }

        final int[] adgOutIndices = config.getAdgOutIndices();
        if (adgOutIndices.length > 3) {
            throw new IllegalArgumentException("Invalid number of adg_out indices: " + adgOutIndices.length + ". Must be in [0, 3]");
        }
        for (int index : adgOutIndices) {
            if (index < 0 || index > 2) {
                throw new IllegalArgumentException("Invalid adg_out index: " + index + ". Must be in [0, 2]");
            }
        }

        final int[] bbpOutIndices = config.getBbpOutIndices();
        if (bbpOutIndices.length > 5) {
            throw new IllegalArgumentException("Invalid number of bbp_out indices: " + bbpOutIndices.length + ". Must be in [0, 5]");
        }
        for (int index : bbpOutIndices) {
            if (index < 0 || index > 4) {
                throw new IllegalArgumentException("Invalid bbp_out index: " + index + ". Must be in [0, 4]");
            }
        }
    }
}
