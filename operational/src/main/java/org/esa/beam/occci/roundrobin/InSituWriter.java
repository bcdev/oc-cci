package org.esa.beam.occci.roundrobin;


import org.esa.beam.occci.bandshift.Sensor;

import java.io.OutputStream;
import java.io.PrintWriter;

class InSituWriter {

    private final PrintWriter printWriter;
    private final String headerLine;

    static InSituWriter create(String sensorName, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream is null.");
        }

        final StringBuilder headerLineBuilder = new StringBuilder(512);
        headerLineBuilder.append("time\tlat\tlon\t");
        if (Sensor.MERIS_NAME.equalsIgnoreCase(sensorName)) {
            headerLineBuilder.append("rrs_413\trrs_443\trrs_490\trrs_510\trrs_560\trrs_620\trrs_665");
        } else if (Sensor.MODISA_NAME.equalsIgnoreCase(sensorName)) {
            headerLineBuilder.append("rrs_412\trrs_443\trrs_488\trrs_531\trrs_547\trrs_667\trrs_678");
        } else if (Sensor.SEAWIFS_NAME.equalsIgnoreCase(sensorName)) {
            headerLineBuilder.append("rrs_412\trrs_443\trrs_490\trrs_510\trrs_555\trrs_670");
        } else {
            throw new IllegalArgumentException("Unsupported sensor: " + sensorName);
        }
        headerLineBuilder.append("\tsubdataset_rrs_1");
        return new InSituWriter(outputStream, headerLineBuilder.toString());
    }

    InSituWriter(OutputStream outputStream, String headerLine) {
        printWriter = new PrintWriter(outputStream);
        this.headerLine = headerLine;
    }

    void writeHeader() {
        printWriter.println(headerLine);
    }

    void write(double[] rss, SpectralMeasurement[] originalMeasurements, InSituSpectrum originalSpectrum) {
        if (rss == null) {
            return;
        }
        final StringBuilder builder = new StringBuilder(512);
        builder.append(originalSpectrum.getDateTime());
        builder.append("\t");
        builder.append(originalSpectrum.getLat());
        builder.append("\t");
        builder.append(originalSpectrum.getLon());
        builder.append("\t");

        for (double rs : rss) {
            builder.append(rs);
            builder.append("\t");
        }

        builder.append(originalSpectrum.getSubdatasetRrs_1());
        builder.append("\t");

        for (SpectralMeasurement spectralMeasurement : originalMeasurements) {
            builder.append(spectralMeasurement.getMeasurement());
            builder.append("\t");
            builder.append(spectralMeasurement.getWavelength());
            builder.append("\t");
        }

        printWriter.println(builder.toString());
    }

    public void close() {
        printWriter.flush();
        printWriter.close();
    }
}
