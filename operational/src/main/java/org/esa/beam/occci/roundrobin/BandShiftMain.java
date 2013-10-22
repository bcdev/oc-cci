package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.qaa.SensorConfig;
import org.esa.beam.occci.qaa.SensorConfigFactory;
import org.esa.beam.util.io.CsvReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BandShiftMain {

    public static void main(String[] args) throws IOException {
        final File inSituCsv = new File(args[0]);
        final FileReader fileReader = new FileReader(inSituCsv);

        final CsvReader csvReader = new CsvReader(fileReader, new char[]{','});
        csvReader.readRecord(); // skip heading line

        String[] record;

        while ((record = csvReader.readRecord()) != null) {
            final InSituSpectrum spectrum = SpectrumBuilder.create(record);
            if (!spectrum.isComplete()) {
                continue;
            }

            final SensorConfig sensorConfig = SensorConfigFactory.get(spectrum.getWavelengths());
        }
    }
}
