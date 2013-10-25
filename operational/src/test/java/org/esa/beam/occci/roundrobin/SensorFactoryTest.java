package org.esa.beam.occci.roundrobin;

import org.esa.beam.occci.bandshift.Sensor;
import org.junit.Test;

import static org.junit.Assert.*;

public class SensorFactoryTest {

    @Test
    public void testCreateMerisLikeToMeris() {
        final double[] inSituWavelengths = {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0};

        final Sensor merisLike = SensorFactory.createMerisLikeToMeris(inSituWavelengths);
        assertNotNull(merisLike);
        assertArrayEquals(new double[] {412.0, 443.0, 490.0, 510.0, 560.0, 620.0, 665.0, 665.0}, merisLike.getLambdaI(), 1e-8);

    }
}
