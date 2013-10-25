package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.BandShiftCorrection;
import org.esa.beam.occci.bandshift.CorrectionContext;
import org.esa.beam.occci.bandshift.Sensor;

import java.io.IOException;

class BandShifter {

    /**
     * Shifts the incoming spectrum to MERIS bands.
     *
     * @return rrs at {413, 443, 490, 510, 560, 620, 665}
     */
    static double[] toMeris(InSituSpectrum spectrum, double[] qaaAt443) throws IOException {
        final Sensor toMeris = SensorFactory.createMerisLikeToMeris(spectrum.getMerisWavelengths());
        final CorrectionContext context = new CorrectionContext(toMeris);
        final BandShiftCorrection bandShiftCorrection = new BandShiftCorrection(context);
        final double[] rrs_corrected = bandShiftCorrection.correctBandshift(spectrum.getMerisMeasurements(), spectrum.getMerisWavelengths(), qaaAt443);
        return bandShiftCorrection.weightedAverageEqualCorrectionProducts(rrs_corrected);
    }
}
