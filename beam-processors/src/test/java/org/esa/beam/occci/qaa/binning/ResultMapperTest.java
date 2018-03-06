package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.qaa.QaaResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResultMapperTest {

    @Test
    public void testAssign_aph() {
        final QaaConfig config = new QaaConfig();
        config.setAphOutIndices(new int[]{1, 2});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[2]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(2.f, vector.get(0), 1e-8);
        assertEquals(3.f, vector.get(1), 1e-8);
    }

    @Test
    public void testAssign_atot() {
        final QaaConfig config = new QaaConfig();
        config.setAtotOutIndices(new int[]{1, 3, 4});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[3]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(5.f, vector.get(0), 1e-8);
        assertEquals(7.f, vector.get(1), 1e-8);
        assertEquals(8.f, vector.get(2), 1e-8);
    }

    @Test
    public void testAssign_adg() {
        final QaaConfig config = new QaaConfig();
        config.setAdgOutIndices(new int[]{0, 2});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[2]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(9.f, vector.get(0), 1e-8);
        assertEquals(11.f, vector.get(1), 1e-8);
    }

    @Test
    public void testAssign_bbp() {
        final QaaConfig config = new QaaConfig();
        config.setBbpOutIndices(new int[]{0, 2, 4});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[3]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(12.f, vector.get(0), 1e-8);
        assertEquals(14.f, vector.get(1), 1e-8);
        assertEquals(16.f, vector.get(2), 1e-8);
    }

    @Test
    public void testAssign_no_output() {
        final QaaConfig config = new QaaConfig();

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[0]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);
    }

    @Test
    public void testAssign_mixed() {
        final QaaConfig config = new QaaConfig();
        config.setAphOutIndices(new int[]{0});
        config.setAtotOutIndices(new int[]{1});
        config.setAdgOutIndices(new int[]{2});
        config.setBbpOutIndices(new int[]{3});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[4]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(1.f, vector.get(0), 1e-8);
        assertEquals(5.f, vector.get(1), 1e-8);
        assertEquals(11.f, vector.get(2), 1e-8);
        assertEquals(15.f, vector.get(3), 1e-8);
    }

    private QaaResult createQaaResult() {
        final QaaResult qaaResult = new QaaResult();
        qaaResult.setAph(1.f, 0);
        qaaResult.setAph(2.f, 1);
        qaaResult.setAph(3.f, 2);

        qaaResult.setAtot(4.f, 0);
        qaaResult.setAtot(5.f, 1);
        qaaResult.setAtot(6.f, 2);
        qaaResult.setAtot(7.f, 3);
        qaaResult.setAtot(8.f, 4);

        qaaResult.setAdg(9.f, 0);
        qaaResult.setAdg(10.f, 1);
        qaaResult.setAdg(11.f, 2);

        qaaResult.setBbp(12.f, 0);
        qaaResult.setBbp(13.f, 1);
        qaaResult.setBbp(14.f, 2);
        qaaResult.setBbp(15.f, 3);
        qaaResult.setBbp(16.f, 4);
        return qaaResult;
    }
}
