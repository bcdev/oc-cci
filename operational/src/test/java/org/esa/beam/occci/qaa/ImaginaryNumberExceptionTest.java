package org.esa.beam.occci.qaa;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImaginaryNumberExceptionTest {

    @Test
    public void testConstructionGetMessage() {
        final ImaginaryNumberException exception = new ImaginaryNumberException("whatever", 56.98);
        assertEquals("whatever: 56.98", exception.getMessage());
    }
}
