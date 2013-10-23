package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.BandShiftCorrection;
import org.esa.beam.occci.bandshift.CorrectionContext;
import org.esa.beam.occci.bandshift.Sensor;
import org.esa.beam.occci.qaa.*;
import org.esa.beam.util.io.CsvReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BandShiftMain {

    public static void main(String[] args) throws IOException, ImaginaryNumberException {
        final File inSituCsv = new File(args[0]);
        final FileReader fileReader = new FileReader(inSituCsv);

        final CsvReader csvReader = new CsvReader(fileReader, new char[]{','});
        csvReader.readRecord(); // skip heading line

        final BandShiftCorrection bsModis = new BandShiftCorrection(new CorrectionContext(Sensor.MODISA));
        final BandShiftCorrection bsMeris = new BandShiftCorrection(new CorrectionContext(Sensor.MERIS));
        final BandShiftCorrection bsSeaWiFS= new BandShiftCorrection(new CorrectionContext(Sensor.SEAWIFS));

        String[] record;
        while ((record = csvReader.readRecord()) != null) {
            final InSituSpectrum spectrum = SpectrumBuilder.create(record);
            if (!spectrum.isCompleteQaa()) {
                continue;
            }

            final SensorConfig sensorConfig = SensorConfigFactory.get(spectrum.getWavelengths());
            final QaaAlgorithm qaaAlgorithm = new QaaAlgorithm(sensorConfig);
            final QaaResult qaaResult = qaaAlgorithm.process(spectrum.getMeasurementsFloat(), null);

            final double[] qaaAt443 = new double[3];
            qaaAt443[0] = qaaResult.getA_PIG()[1];
            qaaAt443[1] = qaaResult.getA_YS()[1];
            qaaAt443[2] = qaaResult.getBB_SPM()[1];

            final double[] merisBS_rrs = bsMeris.correctBandshift(spectrum.getMeasurements(), spectrum.getWavelengths(), qaaAt443);
            final double[] modisBS_rrs = bsModis.correctBandshift(spectrum.getMeasurements(), spectrum.getWavelengths(), qaaAt443);
            final double[] seawifsBS_rrs = bsSeaWiFS.correctBandshift(spectrum.getMeasurements(), spectrum.getWavelengths(), qaaAt443);
        }
    }
}
