package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.PostProcessor;
import org.esa.beam.binning.PostProcessorConfig;
import org.esa.beam.binning.PostProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.support.VariableContextImpl;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.qaa.SensorConfig;
import org.esa.beam.occci.qaa.SensorConfigFactory;

import java.util.ArrayList;

public class QaaDescriptor implements PostProcessorDescriptor {

    @Override
    public String getName() {
        return "QAA";
    }

    @Override
    public PostProcessorConfig createPostProcessorConfig() {
        return new QaaConfig();
    }

    @Override
    public PostProcessor createPostProcessor(VariableContext varCtx, PostProcessorConfig postProcessorConfig) {
        final QaaConfig config = (QaaConfig) postProcessorConfig;
        final String[] outputFeatureNames = createOutputFeatureNames(config);
        return new QaaPostProcessor(new VariableContextImpl(), config, outputFeatureNames);
    }

    // package access for testing only tb 2013-04-23
    static String[] createOutputFeatureNames(QaaConfig config) {
        final SensorConfig sensorConfig = SensorConfigFactory.get(config.getSensorName());
        final ArrayList<String> featureNameList = new ArrayList<String>();

        final double[] wavelengths = sensorConfig.getWavelengths();
        final int[] a_pig_out_indices = config.getA_pig_out_indices();
        for (int a_pig_out_index : a_pig_out_indices) {
            featureNameList.add("a_pig_" + getWavelengthInt(wavelengths, a_pig_out_index));
        }

        final int[] a_total_out_indices = config.getA_total_out_indices();
        for (int a_total_out_index : a_total_out_indices) {
            featureNameList.add("a_total_" + getWavelengthInt(wavelengths, a_total_out_index));
        }

        final int[] a_ys_out_indices = config.getA_ys_out_indices();
        for (int a_ys_out_index : a_ys_out_indices) {
            featureNameList.add("a_ys_" + getWavelengthInt(wavelengths, a_ys_out_index));
        }

        final int[] bb_spm_out_indices = config.getBb_spm_out_indices();
        for (int bb_spm_out_index : bb_spm_out_indices) {
            featureNameList.add("bb_spm_" + getWavelengthInt(wavelengths, bb_spm_out_index));
        }

        return featureNameList.toArray(new String[featureNameList.size()]);
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

        final int[] a_pig_out_indices = config.getA_pig_out_indices();
        if (a_pig_out_indices.length > 3) {
            throw new IllegalArgumentException("Invalid number of a_pig_out indices: " + a_pig_out_indices.length + ". Must be in [0, 3]");
        }
        for (int a_pig_out_index : a_pig_out_indices) {
            if (a_pig_out_index < 0 || a_pig_out_index > 2) {
                throw new IllegalArgumentException("Invalid a_pig_out index: " + a_pig_out_index + ". Must be in [0, 2]");
            }
        }

        final int[] a_total_out_indices = config.getA_total_out_indices();
        if (a_total_out_indices.length > 5) {
            throw new IllegalArgumentException("Invalid number of a_total_out indices: " + a_total_out_indices.length + ". Must be in [0, 5]");
        }
        for (int a_total_out_index : a_total_out_indices) {
            if (a_total_out_index < 0 || a_total_out_index > 4) {
                throw new IllegalArgumentException("Invalid a_total_out index: " + a_total_out_index + ". Must be in [0, 4]");
            }
        }

        final int[] a_ys_out_indices = config.getA_ys_out_indices();
        if (a_ys_out_indices.length > 3) {
            throw new IllegalArgumentException("Invalid number of a_ys_out indices: " + a_ys_out_indices.length + ". Must be in [0, 3]");
        }
        for (int a_ys_out_index : a_ys_out_indices) {
            if (a_ys_out_index < 0 || a_ys_out_index > 2) {
                throw new IllegalArgumentException("Invalid a_ys_out index: " + a_ys_out_index + ". Must be in [0, 2]");
            }
        }
    }
}
