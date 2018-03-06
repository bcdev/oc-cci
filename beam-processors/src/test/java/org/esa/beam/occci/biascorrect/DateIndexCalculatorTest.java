package org.esa.beam.occci.biascorrect;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateIndexCalculatorTest {

    @Test
    public void testGetDateIndex() {
        final DateIndexCalculator dateIndexCalculator = new DateIndexCalculator(2009, 2011);

        int idx = dateIndexCalculator.get(3602.2756);     // 2009-11
        assertEquals(10, idx);                  // year 0, month 10

        idx = dateIndexCalculator.get(3297.7842);         // 2009-01
        assertEquals(0, idx);                   // year 0, month 0

        idx = dateIndexCalculator.get(3653.6107);         // 2010-01
        assertEquals(12, idx);                  // year 1, month 0

        idx = dateIndexCalculator.get(4127.8245);         // 2011-04
        assertEquals(27, idx);                  // year 2, month 3
    }

    @Test
    public void testGetDateIndex_outOfRange() {
        final DateIndexCalculator dateIndexCalculator = new DateIndexCalculator(2009, 2011);

        int idx = dateIndexCalculator.get(3285.8762);     // 2008-12
        assertEquals(DateIndexCalculator.INVALID, idx);                  // out of range

        idx = dateIndexCalculator.get(4428.1734);         // 2012-02
        assertEquals(DateIndexCalculator.INVALID, idx);                  // out of range
    }

    @Test
    public void testGetIndexCount() {
        DateIndexCalculator dateIndexCalculator = new DateIndexCalculator(2008, 2011);
        assertEquals(48, dateIndexCalculator.getIndexCount());

        dateIndexCalculator = new DateIndexCalculator(2005, 2010);
        assertEquals(72, dateIndexCalculator.getIndexCount());

        dateIndexCalculator = new DateIndexCalculator(2007, 2007);
        assertEquals(12, dateIndexCalculator.getIndexCount());
    }

    @Test
    public void testGetNumYears() {
        DateIndexCalculator dateIndexCalculator = new DateIndexCalculator(2008, 2011);
        assertEquals(4, dateIndexCalculator.getNumYears());

        dateIndexCalculator = new DateIndexCalculator(2005, 2010);
        assertEquals(6, dateIndexCalculator.getNumYears());

        dateIndexCalculator = new DateIndexCalculator(2004, 2004);
        assertEquals(1, dateIndexCalculator.getNumYears());
    }
}

