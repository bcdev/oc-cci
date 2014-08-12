package org.esa.beam.occci.roundrobin;


import org.esa.beam.occci.bandshift.Sensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InSituGroupWriter {

    private final InSituWriter merisWriter;
    private final InSituWriter modisWriter;
    private final InSituWriter seawifsWriter;

    InSituGroupWriter(File outputDirectory) throws IOException {
        final FileOutputStream merisOutputStream = createFilestream(outputDirectory, "insitu_rrs_MERIS_bands.txt");
        merisWriter = InSituWriter.create(Sensor.MERIS_NAME, merisOutputStream);

        final FileOutputStream modisOutputStream = createFilestream(outputDirectory, "insitu_rrs_MODIS_bands.txt");
        modisWriter = InSituWriter.create(Sensor.MODISA_NAME, modisOutputStream);

        final FileOutputStream seawifsOutputStream = createFilestream(outputDirectory, "insitu_rrs_SEAWIFS_bands.txt");
        seawifsWriter = InSituWriter.create(Sensor.SEAWIFS_NAME, seawifsOutputStream);
    }

    void writeHeader() {
        merisWriter.writeHeader();
        modisWriter.writeHeader();
        seawifsWriter.writeHeader();
    }

    void write(double[] meris, double[] modis, double[] seaWifs, InSituSpectrum originalSpectrum) {
        merisWriter.write(meris, originalSpectrum);
        modisWriter.write(modis, originalSpectrum);
        seawifsWriter.write(seaWifs, originalSpectrum);
    }

    void writeWithOriginal(double[] meris, double[] modis, double[] seaWifs, InSituSpectrum originalSpectrum) {
        SpectralMeasurement[] originalMeasurements;
        if (originalSpectrum.isCompleteMeris()) {
            originalMeasurements = originalSpectrum.getMerisMeasurementsArray();
        } else if (originalSpectrum.isCompleteModis()) {
            originalMeasurements = originalSpectrum.getModisMeasurementsArray();
        } else if (originalSpectrum.isCompleteSeaWiFS()) {
            originalMeasurements = originalSpectrum.getSeaWiFSsMeasurementsArray();
        } else {
            originalMeasurements = originalSpectrum.getQaaMeasurementsArray();
        }

        merisWriter.write(meris, originalMeasurements, originalSpectrum);
        modisWriter.write(modis, originalMeasurements, originalSpectrum);
        seawifsWriter.write(seaWifs, originalMeasurements, originalSpectrum);
    }

    void close() {
        merisWriter.close();
        modisWriter.close();
        seawifsWriter.close();
    }

    private FileOutputStream createFilestream(File outputDirectory, String fielName) throws IOException {
        final File modisFile = new File(outputDirectory, fielName);
        if (!modisFile.createNewFile()) {
            throw new IOException("Unable to create file: " + modisFile.getAbsolutePath());
        }

        return new FileOutputStream(modisFile);
    }
}
