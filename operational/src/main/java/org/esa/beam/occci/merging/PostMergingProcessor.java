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
import org.esa.beam.occci.qaa.ImaginaryNumberException;
import org.esa.beam.occci.qaa.QaaAlgorithm;
import org.esa.beam.occci.qaa.QaaConstants;
import org.esa.beam.occci.qaa.QaaResult;
import org.esa.beam.occci.qaa.SensorConfig;
import org.esa.beam.occci.qaa.SensorConfigFactory;
import org.esa.beam.occci.qaa.binning.QaaConfig;
import org.esa.beam.occci.qaa.binning.QaaDescriptor;
import org.esa.beam.occci.qaa.binning.ResultMapper;
import org.esa.beam.occci.util.binning.BinningUtils;

import java.util.ArrayList;
import java.util.Collections;

public class PostMergingProcessor extends CellProcessor {

    private final QaaAlgorithm qaaAlgorithm;
    private final int[] rrsBandIndices;
    private final float[] rrs;
    QaaResult qaaResult;
    private final ResultMapper resultMapper;

    public PostMergingProcessor(VariableContext varCtx, QaaConfig qaaConfig) {
        super(createOutputFeatureNames(varCtx, qaaConfig));

        final SensorConfig sensorConfig = SensorConfigFactory.get(qaaConfig.getSensorName());
        qaaAlgorithm = new QaaAlgorithm(sensorConfig);

        final String[] bandNames = qaaConfig.getBandNames();
        rrsBandIndices = BinningUtils.getBandIndices(varCtx, bandNames);

        rrs = new float[bandNames.length];
        qaaResult = new QaaResult();
        resultMapper = new ResultMapper(qaaConfig);
    }

    @Override
    public void compute(Vector inputVector, WritableVector outputVector) {
        for (int i = 0; i < rrs.length; i++) {
            rrs[i] = inputVector.get(rrsBandIndices[i]);
        }

        try {
            qaaResult = qaaAlgorithm.process(rrs, qaaResult);
        } catch (ImaginaryNumberException e) {
            BinningUtils.setToInvalid(outputVector);
            return;
        }

        resultMapper.assign(qaaResult, rrs, outputVector);
    }

    public static class Config extends CellProcessorConfig {

        public Config() {
            super(Descriptor.NAME);
        }

    }

    public static class Descriptor implements CellProcessorDescriptor {

        public static final String NAME = "PostMerging";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public CellProcessor createCellProcessor(VariableContext varCtx, CellProcessorConfig cellProcessorConfig) {
            QaaConfig qaaConfig = new QaaConfig();
            qaaConfig.setSensorName(QaaConstants.SEAWIFS);
            qaaConfig.setBandNames(new String[]{"Rrs_412","Rrs_443","Rrs_490","Rrs_510","Rrs_555","Rrs_670"});
            qaaConfig.setATotalOutIndices(new int[]{0, 1, 2, 3, 4});
            qaaConfig.setBbSpmOutIndices(new int[]{0, 1, 2, 3, 4});
            qaaConfig.setAPigOutIndices(new int[]{0, 1, 2});
            qaaConfig.setAYsOutIndices(new int[]{0, 1, 2});
            qaaConfig.setRrsOut(true);
            return new PostMergingProcessor(varCtx, qaaConfig);
        }

        @Override
        public CellProcessorConfig createConfig() {
            return new Config();
        }
    }

    private static String[] createOutputFeatureNames(VariableContext varCtx, QaaConfig qaaConfig) {
        String[] outputFeatureNames = QaaDescriptor.createOutputFeatureNames(qaaConfig);
        final ArrayList<String> featureNameList = new ArrayList<String>();
        Collections.addAll(featureNameList, outputFeatureNames);

        for (int i = 0; i < SensorMerging.SENSORS.length; i++) {
            featureNameList.add("sensor_" + i);
        }
        return featureNameList.toArray(new String[featureNameList.size()]);
    }
}
