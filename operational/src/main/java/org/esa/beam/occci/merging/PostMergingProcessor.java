/*
 * Copyright (C) 2013 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.occci.merging;


import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.Vector;
import org.esa.beam.binning.WritableVector;
import org.esa.beam.coastcolour.fuzzy.Auxdata;
import org.esa.beam.coastcolour.fuzzy.FuzzyClassification;
import org.esa.beam.occci.qaa.QaaAlgorithm;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.qaa.QaaResult;
import org.esa.beam.occci.qaa.SensorConfig;
import org.esa.beam.occci.qaa.SensorConfigFactory;
import org.esa.beam.occci.qaa.binning.QaaCellProcessor;
import org.esa.beam.occci.qaa.binning.QaaConfig;
import org.esa.beam.occci.qaa.binning.ResultMapper;
import org.esa.beam.occci.util.binning.BinningUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

@Deprecated
public class PostMergingProcessor extends CellProcessor {
    private static final String[] BAND_NAMES = new String[]{"Rrs_412", "Rrs_443", "Rrs_490", "Rrs_510", "Rrs_555", "Rrs_670"};

    //    private static final String AUXDATA_PATH = "owt16_meris_stats_101119_5band.hdf";
    private static final String AUXDATA_PATH = "owt16_seawifs_stats_101111.hdf";


    private static final int i443 = 1;
    private static final int i489 = 2;
    private static final int i510 = 3;
    private static final int i550 = 4;

    private final QaaAlgorithm qaaAlgorithm;
    private final int[] rrsBandIndices;
    private final int[] sensorBandIndices;
    private final float[] rrs;
    private final float[] sensor;
    private final int rrsOffset;
    private final int sensorOffset;
    private final int owtOffset;
    private final QaaResult qaaResult;
    private final ResultMapper resultMapper;
    private final FuzzyClassification fuzzyClassification;
    private final int owtInputBandCount;

    public PostMergingProcessor(VariableContext varCtx, QaaConfig qaaConfig) {
        super(createOutputFeatureNames(varCtx, qaaConfig));

        final SensorConfig sensorConfig = SensorConfigFactory.get(qaaConfig.getSensorName());
        qaaAlgorithm = new QaaAlgorithm(sensorConfig);

        rrsOffset = 4 * 6 + 1;
        sensorOffset = 4 * 6 + 1 + 6;
        owtOffset = 4 * 6 + 1 + 6 + 3;

        final String[] bandNames = qaaConfig.getBandNames();
        rrsBandIndices = BinningUtils.getBandIndices(varCtx, bandNames);
        sensorBandIndices = BinningUtils.getBandIndices(varCtx, "sensor_0", "sensor_1", "sensor_2");

        rrs = new float[rrsBandIndices.length];
        sensor = new float[sensorBandIndices.length];
        qaaResult = new QaaResult();
        resultMapper = new ResultMapper(qaaConfig);

        final URL resourceUrl = FuzzyClassification.class.getResource(AUXDATA_PATH);
        try {
            final Auxdata auxdata = new Auxdata(resourceUrl.toURI());
            owtInputBandCount = auxdata.getSpectralMeans().length;
            fuzzyClassification = new FuzzyClassification(auxdata.getSpectralMeans(),
                                                          auxdata.getInvertedCovarianceMatrices());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load auxdata", e);
        }

    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < rrs.length; i++) {
            rrs[i] = inputVector.get(rrsBandIndices[i]);
            if (Float.isNaN(rrs[i])) {
                BinningUtils.setToInvalid(outputVector);
                return;
            }
        }
        for (int i = 0; i < sensor.length; i++) {
            sensor[i] = inputVector.get(sensorBandIndices[i]);
        }
        qaaAlgorithm.process(rrs, qaaResult);
        qaaResult.infinityAsNaN();

        resultMapper.assign(qaaResult, outputVector);
        copyRRS(rrs, outputVector);
        copySensorContribution(sensor, outputVector);

        computeOWT(rrs, outputVector);
        outputVector.set(24, computeChlOc4v6(rrs));
    }

    private void computeOWT(float[] rrs, WritableVector outputVector) {
        double[] rrsBelowWater = new double[owtInputBandCount];
        for (int i = 0; i < rrsBelowWater.length; i++) {
            rrsBelowWater[i] = convertToSubsurfaceWaterRrs(rrs[i]);
        }

        double[] membershipIndicators = fuzzyClassification.computeClassMemberships(rrsBelowWater);
        // setting the values for the first 8 classes
        double ninthClassValue = 0.0;
        for (int i = 0; i < 8; i++) {
            double membershipIndicator = membershipIndicators[i];
            outputVector.set(owtOffset + i, (float) membershipIndicator);
            ninthClassValue += membershipIndicator;
        }
        // setting the value for the 9th class to the sum of the last 8 classes
        outputVector.set(owtOffset + 8, (float) ninthClassValue);
    }

    // TODO is this right ?
    private static double convertToSubsurfaceWaterRrs(double merisL2Reflec) {
        // convert to remote sensing reflectances
        final double rrsAboveWater = merisL2Reflec / Math.PI;
        // convert to subsurface water remote sensing reflectances
        return rrsAboveWater / (0.52 + 1.7 * rrsAboveWater);
    }

    float computeChlOc4v6(float[] rrs) {

        final double[] chloc4_coef = {0.3272f, -2.9940f, 2.7218f, -1.2259f, -0.5683f};
        final double minrat = 0.21f;
        final double maxrat = 30.0f;
        final double chlmin = 0.0;
        final double chlmax = 1000.0;

        double rat, minRrs;
        double chl = Double.NaN;

        double rrs1 = rrs[i443];
        double rrs2 = rrs[i489];
        double rrs3 = rrs[i510];
        double rrs4 = rrs[i550];


        minRrs = Math.min(rrs1, rrs2);

        if (rrs4 > 0.0 && rrs3 > 0.0 && (rrs2 > 0.0 || rrs1 * rrs2 > 0.0) && minRrs > -0.001) {
            rat = Math.max(Math.max(rrs1, rrs2), rrs3) / rrs4;
            if (rat > minrat && rat < maxrat) {
                rat = Math.log10(rat);
                chl = Math.pow(10.0, (chloc4_coef[0] + rat * (chloc4_coef[1] + rat * (chloc4_coef[2] + rat * (chloc4_coef[3] + rat * chloc4_coef[4])))));
                chl = (chl > chlmin ? chl : chlmin);
                chl = (chl < chlmax ? chl : chlmax);
            }
        }

        return (float) chl;
    }

    private void copyRRS(float[] rrs, WritableVector outputVector) {
        for (int i = 0; i < rrs.length; i++) {
            outputVector.set(rrsOffset + i, rrs[i]);
        }
    }

    private void copySensorContribution(float[] sensors, WritableVector outputVector) {
        for (int i = 0; i < sensors.length; i++) {
            outputVector.set(sensorOffset + i, sensors[i]);
        }
    }

    public static class Config extends CellProcessorConfig {

        public Config() {
            super(Descriptor.NAME);
        }

    }

    public static class Descriptor implements CellProcessorDescriptor {

        public static final String NAME = "PostMerging";
        private static final int[] ALL_IOPS = new int[]{0, 1, 2, 3, 4, 5};


        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig cellProcessorConfig) {
            QaaConfig qaaConfig = new QaaConfig();
            qaaConfig.setSensorName(QaaConstants.SEAWIFS);
            qaaConfig.setBandNames(BAND_NAMES);
            qaaConfig.setATotalOutIndices(ALL_IOPS);
            qaaConfig.setBbSpmOutIndices(ALL_IOPS);
            qaaConfig.setAPigOutIndices(ALL_IOPS);
            qaaConfig.setAYsOutIndices(ALL_IOPS);
            return new PostMergingProcessor(varCtx, qaaConfig);
        }

        @Override
        public CellProcessorConfig createConfig() {
            return new Config();
        }
    }

    private static String[] createOutputFeatureNames(VariableContext varCtx, QaaConfig qaaConfig) {
        String[] IOPfeatureNames = QaaCellProcessor.createOutputFeatureNames(qaaConfig);
        final ArrayList<String> featureNameList = new ArrayList<String>();
        Collections.addAll(featureNameList, IOPfeatureNames);
        featureNameList.add("chlor_a");
        Collections.addAll(featureNameList, BAND_NAMES);

        for (int i = 0; i < SensorMerging.SENSORS.length; i++) {
            featureNameList.add("sensor_" + i);
        }
        for (int i = 1; i <= 9; i++) {
            featureNameList.add("water_class" + i);
        }

        return featureNameList.toArray(new String[featureNameList.size()]);
    }
}
