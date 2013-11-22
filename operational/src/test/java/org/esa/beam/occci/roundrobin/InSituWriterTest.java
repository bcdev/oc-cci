package org.esa.beam.occci.roundrobin;


import org.esa.beam.occci.bandshift.Sensor;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class InSituWriterTest {

    private String newline;

    @Before
    public void setUp() {
        newline = System.getProperty("line.separator");
    }

    @Test
    public void testCreate() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

        InSituWriter writer = InSituWriter.create("MERIS", outputStream);
        assertNotNull(writer);

        writer = InSituWriter.create("MODISA", outputStream);
        assertNotNull(writer);

        writer = InSituWriter.create("SEAWIFS", outputStream);
        assertNotNull(writer);
    }

    @Test
    public void testCreate_invalidInput() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

        try {
            InSituWriter.create("SENTINEL", outputStream);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            InSituWriter.create("MODISA", null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWriteHeaderLine_MERIS() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        final InSituWriter merisWriter = InSituWriter.create(Sensor.MERIS_NAME, outputStream);

        merisWriter.writeHeader();
        merisWriter.close();

        assertEquals("time\tlat\tlon\trrs_413\trrs_443\trrs_490\trrs_510\trrs_560\trrs_620\trrs_665\tsubdataset_rrs_1" + newline, outputStream.toString());
    }

    @Test
    public void testWriteHeaderLine_MODIS() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        final InSituWriter merisWriter = InSituWriter.create(Sensor.MODISA_NAME, outputStream);

        merisWriter.writeHeader();
        merisWriter.close();

        assertEquals("time\tlat\tlon\trrs_412\trrs_443\trrs_488\trrs_531\trrs_547\trrs_667\trrs_678\tsubdataset_rrs_1" + newline, outputStream.toString());
    }

    @Test
    public void testWriteHeaderLine_SeaWiFS() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        final InSituWriter merisWriter = InSituWriter.create(Sensor.SEAWIFS_NAME, outputStream);

        merisWriter.writeHeader();
        merisWriter.close();

        assertEquals("time\tlat\tlon\trrs_412\trrs_443\trrs_490\trrs_510\trrs_555\trrs_670\tsubdataset_rrs_1" + newline, outputStream.toString());
    }

    // @todo 2 tb/tb add tests for writing tb 2013-11-11
}
