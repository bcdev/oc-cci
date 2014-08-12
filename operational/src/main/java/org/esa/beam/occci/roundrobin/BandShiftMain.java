package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.qaa.*;
import org.esa.beam.util.io.CsvReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BandShiftMain {


    public static void main(String[] args) throws IOException {
        final CsvReader csvReader = createInputCsvReader(args[0]);

        final InSituGroupWriter inSituGroupWriter = new InSituGroupWriter(new File("."));
        inSituGroupWriter.writeHeader();

        try {
            String[] record;
            int countReadRecords = 0;
            int countShiftedRecords = 0;
            while ((record = csvReader.readRecord()) != null) {
                countReadRecords++;
                final InSituSpectrum spectrum = SpectrumBuilder.create(record);
                if (!spectrum.isCompleteQaa()) {
                    continue;
                }

                final SensorConfig sensorConfig = SensorConfigFactory.get(spectrum.getQaaWavelengths());
                final QaaAlgorithm qaaAlgorithm = new QaaAlgorithm(sensorConfig);
                final QaaResult qaaResult = qaaAlgorithm.process(spectrum.getMeasurementsFloat(), null);

                final double[] qaaAt443 = new double[3];
                qaaAt443[0] = qaaResult.getAph()[1];
                qaaAt443[1] = qaaResult.getAdg()[1];
                qaaAt443[2] = qaaResult.getBbp()[1];

                final double[] merisRss = BandShifter.toMeris(spectrum, qaaAt443);
                final double[] modisRss = BandShifter.toModis(spectrum, qaaAt443);
                final double[] seaWifsRss = BandShifter.toSeaWifs(spectrum, qaaAt443);

                inSituGroupWriter.write(merisRss, modisRss, seaWifsRss, spectrum);
                countShiftedRecords++;

                System.out.println("read = " + countReadRecords + " shifted = " + countShiftedRecords);
            }
        } finally {
            csvReader.close();
            inSituGroupWriter.close();
        }
    }

    private static CsvReader createInputCsvReader(String arg) throws IOException {
        final File inSituCsv = new File(arg);
        final FileReader fileReader = new FileReader(inSituCsv);

        final CsvReader csvReader = new CsvReader(fileReader, new char[]{','});
        csvReader.readRecord(); // skip heading line
        return csvReader;
    }
}
