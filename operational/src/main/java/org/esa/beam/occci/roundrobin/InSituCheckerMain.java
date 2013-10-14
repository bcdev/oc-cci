package org.esa.beam.occci.roundrobin;

import org.esa.beam.util.StringUtils;
import org.esa.beam.util.io.CsvReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class InSituCheckerMain {

    public static void main(String[] args) throws IOException {
        final File inSituCsv = new File(args[0]);
        final FileReader fileReader = new FileReader(inSituCsv);
        final PrintWriter printWriter = createOutputWriter();

        final CsvReader csvReader = new CsvReader(fileReader, new char[]{','});
        csvReader.readRecord(); // skip heading line

        String[] record;
        int count = 0;
        int countComplete = 0;

        while ((record = csvReader.readRecord()) != null) {
            final InSituSpectrum spectrum = SpectrumBuilder.create(record);
            if (spectrum.isComplete()) {
                ++countComplete;
                final String csvLine = StringUtils.arrayToCsv(record);
                printWriter.println(csvLine);
            }

            ++count;
        }

        csvReader.close();
        printWriter.close();

        System.out.println("count         = " + count);
        System.out.println("countComplete = " + countComplete);

        // /fs1/projects/ongoing/OC-CCI/data/RoundRobin_II/insitu_rrs_ex_cci_noduplicates_SATBANDS_wndspd_chla_iop_kd.txt
    }

    private static PrintWriter createOutputWriter() throws IOException {
        final File consolidatedCsv = new File("insitu_rrs_ex_cci_noduplicates_SATBANDS_qaa_possible.txt");
        if (!consolidatedCsv.createNewFile()) {
            throw new IOException("unable to create file: " + consolidatedCsv.getAbsolutePath());
        }
        return new PrintWriter(consolidatedCsv);
    }
}
