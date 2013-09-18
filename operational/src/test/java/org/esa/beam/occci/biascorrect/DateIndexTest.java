package org.esa.beam.occci.biascorrect;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DateIndexTest {

    @Test
    public void testGetDateIndex() {
        final DateIndex dateIndex = new DateIndex(2009, 2011);

        int idx = dateIndex.get(3602.2756);     // 2009-11
        assertEquals(10, idx);                  // year 0, month 10

        idx = dateIndex.get(3297.7842);         // 2009-01
        assertEquals(0, idx);                   // year 0, month 0

        idx = dateIndex.get(3653.6107);         // 2010-01
        assertEquals(12, idx);                  // year 1, month 0

        idx = dateIndex.get(4127.8245);         // 2011-04
        assertEquals(27, idx);                  // year 2, month 3
    }

    @Test
    public void testGetDateIndex_outOfRange() {
        DateIndex dateIndex = new DateIndex(2009, 2011);

        int idx = dateIndex.get(3285.8762);     // 2008-12
        assertEquals(DateIndex.INVALID, idx);                  // out of range

        idx = dateIndex.get(4428.1734);         // 2012-02
        assertEquals(DateIndex.INVALID, idx);                  // out of range
    }
}

