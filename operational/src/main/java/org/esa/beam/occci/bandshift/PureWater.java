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

package org.esa.beam.occci.bandshift;

import org.esa.beam.util.io.CsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

/**
 * A group of procedures and functions for transferring absorption and scattering coefficient of pure seawater
 * from given files to an Java structure, and for querying this structure
 * to obtain the pure seawater IOPs at specific wavelengths.
 *
 * @author Frédéric Mélin (European Commission/JRC/IES/WRES)
 * @author Gert Sclep (European Commission/JRC/IES/WRES)
 * @author MarcoZ
 */
public class PureWater {

    private static final int DEFAULT_BANDWIDTH = 10;

    private final double[] lambda;
    private final double[] aw;
    private final double[] bw;

    PureWater(double[] lambda, double[] aw, double[] bw) {
        this.lambda = lambda;
        this.aw = aw;
        this.bw = bw;
    }

    double[] getAw() {
        return aw;
    }

    double[] getBw() {
        return bw;
    }

    double[] getLambda() {
        return lambda;
    }

    public static PureWater read() throws IOException {
        InputStreamReader inputStreamReader = openReader("water_spectra.dat");
        PureWater pureWater;
        try {
            pureWater = PureWater.readWater(inputStreamReader);
        } finally {
            inputStreamReader.close();
        }
        inputStreamReader = openReader("bw_spectrum_s35t22.dat");
        PureWater pureBWater;
        try {
            pureBWater = PureWater.readBWater(inputStreamReader);
        } finally {
            inputStreamReader.close();
        }
        // substitute with Zhang et al. values
        return new PureWater(pureWater.getLambda(), pureWater.getAw(), pureBWater.getBw());
    }

    private static InputStreamReader openReader(String name) {
        InputStream resourceAsStream = PureWater.class.getResourceAsStream(name);
        return new InputStreamReader(resourceAsStream);
    }

    static PureWater readWater(Reader reader) throws IOException {
        CsvReader csvReader = new CsvReader(reader, new char[]{' '});
        for (int i = 0; i < 29; i++) {
            csvReader.readLine();
        }
        int count = 0;
        double[] doubles;
        double[] lambda = new double[3000];
        double[] aw = new double[3000];
        double[] bw = new double[3000];
        while ((doubles = csvReader.readDoubleRecord()) != null) {
            lambda[count] = doubles[0];
            aw[count] = doubles[1];
            bw[count] = doubles[2];
            count++;
        }
        return new PureWater(Arrays.copyOf(lambda, count), Arrays.copyOf(aw, count), Arrays.copyOf(bw, count));
    }

    static PureWater readBWater(Reader reader) throws IOException {
        CsvReader csvReader = new CsvReader(reader, new char[]{' '});
        for (int i = 0; i < 15; i++) {
            csvReader.readLine();
        }
        int count = 0;
        double[] doubles;
        double[] lambda = new double[3000];
        double[] bw = new double[3000];
        while ((doubles = csvReader.readDoubleRecord()) != null) {
            lambda[count] = doubles[0];
            bw[count] = doubles[1];
            count++;
        }
        return new PureWater(Arrays.copyOf(lambda, count), null, Arrays.copyOf(bw, count));
    }

    public double[] getSpectralDataPureWater(double wave) {
        return getSpectralDataPureWater(wave, DEFAULT_BANDWIDTH);
    }

    public double[] getSpectralDataPureWater(double wave, int width) {

        // Tests on wave
        double step_size = lambda[1] - lambda[0];
        int waveIndex = (int) Math.floor((wave - lambda[0]) / step_size);

        if (waveIndex < 0 || waveIndex > lambda.length) {
            return null;
        }
        //Get interval and calculate averages
        int waveWindow = (int) (width / 2.0 / step_size);
        int iMin = Math.max(waveIndex - waveWindow, 0);
        int iMax = Math.min(waveIndex + waveWindow, lambda.length);

        double law = mean(aw, iMin, iMax);
        double lbw = mean(bw, iMin, iMax);

        return new double[]{law, lbw};
    }

    static double mean(double[] data, int iMin, int iMax) {
        double sum = 0.0;
        int count = 0;
        for (int i = iMin; i <= iMax; i++) {
            sum += data[i];
            count++;
        }
        return (sum / count);
    }
}
