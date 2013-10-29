package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.qaa.*;
import org.esa.beam.util.io.CsvReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BandShiftMain {

    private static int count = 0;

    public static void main(String[] args) throws IOException, ImaginaryNumberException {
        final File inSituCsv = new File(args[0]);
        final FileReader fileReader = new FileReader(inSituCsv);

        final CsvReader csvReader = new CsvReader(fileReader, new char[]{','});
        csvReader.readRecord(); // skip heading line

        String[] record;
        while ((record = csvReader.readRecord()) != null) {
            final InSituSpectrum spectrum = SpectrumBuilder.create(record);
            if (!spectrum.isCompleteQaa()) {
                continue;
            }

            final SensorConfig sensorConfig = SensorConfigFactory.get(spectrum.getQaaWavelengths());
            final QaaAlgorithm qaaAlgorithm = new QaaAlgorithm(sensorConfig);
            final QaaResult qaaResult = qaaAlgorithm.process(spectrum.getMeasurementsFloat(), null);

            final double[] qaaAt443 = new double[3];
            qaaAt443[0] = qaaResult.getA_PIG()[1];
            qaaAt443[1] = qaaResult.getA_YS()[1];
            qaaAt443[2] = qaaResult.getBB_SPM()[1];

            if (spectrum.isCompleteMeris()) {
                shiftFromMerisLike(spectrum, qaaAt443);
            } else if (spectrum.isCompleteModis()) {
                shiftFromModisLike(spectrum, qaaAt443);
            } else if (spectrum.isCompleteSeaWiFS()) {
                shiftFromSeawifsLike(spectrum, qaaAt443);
            } else {
                shiftFromQaa(spectrum, qaaAt443);
            }
        }
    }

    private static void shiftFromMerisLike(InSituSpectrum spectrum, double[] qaaAt443) {
        System.out.println("shiftFromMerisLike() - TO BE IMPLEMENTED");
    }

    private static void shiftFromModisLike(InSituSpectrum spectrum, double[] qaaAt443) {
        ++count;
        if (count == 529) {
            System.out.println("shiftFromModisLike() - TO BE IMPLEMENTED");
        }
    }

    private static void shiftFromSeawifsLike(InSituSpectrum spectrum, double[] qaaAt443) {
        System.out.println("shiftFromSeawifsLike() - TO BE IMPLEMENTED");
    }

    private static void shiftFromQaa(InSituSpectrum spectrum, double[] qaaAt443) {
        System.out.println("shiftFromQaa() - TO BE IMPLEMENTED");
    }
}
