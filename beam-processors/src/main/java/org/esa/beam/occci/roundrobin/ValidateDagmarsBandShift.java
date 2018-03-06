package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.qaa.QaaAlgorithm;
import org.esa.beam.occci.qaa.QaaResult;
import org.esa.beam.occci.qaa.SensorConfig;
import org.esa.beam.occci.qaa.SensorConfigFactory;
import org.esa.beam.util.io.CsvReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ValidateDagmarsBandShift {
    private static final double[] seaWifsWL = new double[]{412, 443, 490, 510, 555, 670};

    public static void main(String[] args) throws IOException {
        final CsvReader csvReader = createInputCsvReader(args[0]);
        final float[] rssMeasures = new float[6];


        final File outFile = new File("Validation_Test_Bandshift_QAA_valData_RefSens_SEAWIFS_557_6_to_555.csv");
        outFile.createNewFile();
        final PrintWriter printWriter = new PrintWriter(outFile);
        printWriter.println("rrs_ex_SWS1\trrs_ex_SWS2\trrs_ex_SWS3\trrs_ex_SWS4\ttarget.rrs\trrs_ex_SWS6\tlam_SWS1\tlam_SWS2\tlam_SWS3\tlam_SWS4\ttarget.lam\tlam_SWS6");

        try {
            String[] record;
            while ((record = csvReader.readRecord()) != null) {
                final InSituSpectrum spectrum = new InSituSpectrum();

                for (int i = 0; i < 6; i++) {
                    final double rss = Double.parseDouble(record[i]);
                    final double wavelength = Double.parseDouble(record[i + 6]);

                    final SpectralMeasurement measurement = new SpectralMeasurement();
                    measurement.setMeasurement(rss);
                    measurement.setWavelength(wavelength);
                    spectrum.setQaaSpectralValue(measurement, i);
                }

                final double[] qaaMeasurements = spectrum.getQaaMeasurements();
                for (int i = 0; i < qaaMeasurements.length; i++) {
                    rssMeasures[i] = (float) qaaMeasurements[i];
                }

                final SensorConfig sensorConfig = SensorConfigFactory.get(spectrum.getQaaWavelengths());
                final QaaAlgorithm qaaAlgorithm = new QaaAlgorithm(sensorConfig);
                final QaaResult qaaResult = qaaAlgorithm.process(rssMeasures, null);

                final double[] qaaAt443 = new double[3];
                qaaAt443[0] = qaaResult.getAph()[1];
                qaaAt443[1] = qaaResult.getAdg()[1];
                qaaAt443[2] = qaaResult.getBbp()[1];

                final double[] merisRss = BandShifter.toSeaWifs(spectrum, qaaAt443);
                if (merisRss != null) {
                    final StringBuilder stringBuilder = new StringBuilder(512);
                    for (double merisRs : merisRss) {
                        stringBuilder.append(merisRs);
                        stringBuilder.append('\t');
                    }
                    for (int i = 0; i < seaWifsWL.length; i++) {
                        stringBuilder.append(seaWifsWL[i]);
                        if (i < seaWifsWL.length - 1) {
                            stringBuilder.append('\t');
                        }
                    }
                    printWriter.println(stringBuilder.toString());
                } else {
                    printWriter.println("can not correct");
                }
            }
        } finally {
            csvReader.close();
            printWriter.close();
        }
    }

    private static CsvReader createInputCsvReader(String arg) throws IOException {
        final File inSituCsv = new File(arg);
        final FileReader fileReader = new FileReader(inSituCsv);

        final CsvReader csvReader = new CsvReader(fileReader, new char[]{'\t'});
        csvReader.readRecord(); // skip heading line
        return csvReader;
    }
}
