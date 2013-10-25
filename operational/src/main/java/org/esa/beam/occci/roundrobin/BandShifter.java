package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.BandShiftCorrection;
import org.esa.beam.occci.bandshift.CorrectionContext;
import org.esa.beam.occci.bandshift.Sensor;

import java.io.IOException;
import java.util.Arrays;

class BandShifter {

    /**
     * Shifts the incoming spectrum to MERIS bands.
     *
     * @return rrs at {413, 443, 490, 510, 560, 620, 665}
     */
    static double[] toMeris(InSituSpectrum spectrum, double[] qaaAt443) throws IOException {
        final Sensor toMeris = SensorFactory.createToMeris(spectrum);
        final CorrectionContext context = new CorrectionContext(toMeris);
        final BandShiftCorrection bandShiftCorrection = new BandShiftCorrection(context);
        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(spectrum.getMerisMeasurements(), spectrum.getMerisWavelengths(), qaaAt443);
        return bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);
    }

    /**
     * Shifts the incoming spectrum to MODIS bands.
     *
     * @return rrs at {412, 443, 488, 531, 547, 667, 678}
     */
    static double[] toModis(InSituSpectrum spectrum, double[] qaaAt443) throws IOException {
        final Sensor toMeris = SensorFactory.createToModis(spectrum);
        final CorrectionContext context = new CorrectionContext(toMeris);
        final BandShiftCorrection bandShiftCorrection = new BandShiftCorrection(context);
        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(spectrum.getMerisMeasurements(), spectrum.getMerisWavelengths(), qaaAt443);
        return bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);
    }

    /**
     * Shifts the incoming spectrum to SeaWiFS bands.
     *
     * @return rrs at {412, 443, 490, 510, 555, 670}
     */
    static double[] toSeaWifs(InSituSpectrum spectrum, double[] qaaAt443) throws IOException {
        final Sensor toMeris = SensorFactory.createToSeaWifs(spectrum);
        final CorrectionContext context = new CorrectionContext(toMeris);
        final BandShiftCorrection bandShiftCorrection = new BandShiftCorrection(context);
        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(spectrum.getMerisMeasurements(), spectrum.getMerisWavelengths(), qaaAt443);
        final double[] correcteAveraged = bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);
        return Arrays.copyOf(correcteAveraged, correcteAveraged.length - 1);
    }
}
