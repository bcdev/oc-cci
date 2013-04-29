package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.support.VectorImpl;
import org.esa.beam.occci.qaa.QaaResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResultMapperTest {

    @Test
    public void testAssign_a_pig() {
        final QaaConfig config = new QaaConfig();
        config.setAPigOutIndices(new int[]{1, 2});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[2]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(2.f, vector.get(0), 1e-8);
        assertEquals(3.f, vector.get(1), 1e-8);
    }

    @Test
    public void testAssign_a_total() {
        final QaaConfig config = new QaaConfig();
        config.setATotalOutIndices(new int[]{1, 3, 4});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[3]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(5.f, vector.get(0), 1e-8);
        assertEquals(7.f, vector.get(1), 1e-8);
        assertEquals(8.f, vector.get(2), 1e-8);
    }

    @Test
    public void testAssign_a_ys() {
        final QaaConfig config = new QaaConfig();
        config.setAYsOutIndices(new int[]{0, 2});

        final ResultMapper mapper = new ResultMapper(config);
        final VectorImpl vector = new VectorImpl(new float[2]);

        final QaaResult qaaResult = createQaaResult();
        mapper.assign(qaaResult, vector);

        assertEquals(9.f, vector.get(0), 1e-8);
        assertEquals(11.f, vector.get(1), 1e-8);
    }

    @Test
    public void testAssign_bb_spm() {
        final QaaConfig config = new QaaConfig();
        config.setBbSpmOutIndices(new int[]{0, 2, 4});

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
        config.setAPigOutIndices(new int[]{0});
        config.setATotalOutIndices(new int[]{1});
        config.setAYsOutIndices(new int[]{2});
        config.setBbSpmOutIndices(new int[]{3});

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
        qaaResult.setA_PIG(1.f, 0);
        qaaResult.setA_PIG(2.f, 1);
        qaaResult.setA_PIG(3.f, 2);

        qaaResult.setA_Total(4.f, 0);
        qaaResult.setA_Total(5.f, 1);
        qaaResult.setA_Total(6.f, 2);
        qaaResult.setA_Total(7.f, 3);
        qaaResult.setA_Total(8.f, 4);

        qaaResult.setA_YS(9.f, 0);
        qaaResult.setA_YS(10.f, 1);
        qaaResult.setA_YS(11.f, 2);

        qaaResult.setBB_SPM(12.f, 0);
        qaaResult.setBB_SPM(13.f, 1);
        qaaResult.setBB_SPM(14.f, 2);
        qaaResult.setBB_SPM(15.f, 3);
        qaaResult.setBB_SPM(16.f, 4);
        return qaaResult;
    }
}
