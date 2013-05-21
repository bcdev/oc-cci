package org.esa.beam.occci.qaa.binning;


import org.esa.beam.binning.CellProcessor;
import org.esa.beam.binning.CellProcessorConfig;
import org.esa.beam.binning.CellProcessorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.esa.beam.binning.support.VariableContextImpl;
import org.esa.beam.occci.qaa.QaaConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class QaaDescriptorTest {

    private QaaDescriptor descriptor;

    @Before
    public void setUp() {
        descriptor = new QaaDescriptor();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testInterfaceImplemented() {
        assertTrue(descriptor instanceof CellProcessorDescriptor);
    }

    @Test
    public void testGetName() {
        assertEquals("QAA", descriptor.getName());
    }

    @Test
    public void testCreateConfig() {
        final CellProcessorConfig config = descriptor.createConfig();
        assertNotNull(config);
        assertNotNull(config);
        assertTrue(config instanceof QaaConfig);
        assertEquals("QAA", config.getName());
    }

    @Test
    public void testCreateCellProcessor() {
        final QaaConfig config = createValidConfig();
        final VariableContext varCtx = createValidContext();

        final CellProcessor postProcessor = descriptor.createCellProcessor(varCtx, config);
        assertNotNull(postProcessor);
        assertTrue(postProcessor instanceof QaaPostProcessor);
    }


    @Test
    public void testCreateOutputFeatureNames_MERIS_no_bb_spm() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.MERIS);
        config.setAPigOutIndices(new int[]{0, 1});
        config.setATotalOutIndices(new int[]{2, 3});
        config.setAYsOutIndices(new int[]{4, 5});
        config.setBbSpmOutIndices(new int[0]);

        final String[] featureNames = QaaDescriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_pig_413", "a_pig_443", "a_total_490", "a_total_510", "a_ys_560", "a_ys_665"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testCreateOutputFeatureNames_MODIS_no_a_pig() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.MODIS);
        config.setAPigOutIndices(new int[0]);
        config.setATotalOutIndices(new int[]{2, 3});
        config.setAYsOutIndices(new int[]{4});
        config.setBbSpmOutIndices(new int[]{3, 4, 5});

        final String[] featureNames = QaaDescriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_total_488", "a_total_531", "a_ys_547", "bb_spm_531", "bb_spm_547", "bb_spm_667"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testCreateOutputFeatureNames_SEAWIFS_no_a_total() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.SEAWIFS);
        config.setAPigOutIndices(new int[]{0, 1, 5});
        config.setATotalOutIndices(new int[0]);
        config.setAYsOutIndices(new int[]{4});
        config.setBbSpmOutIndices(new int[]{3, 4, 5});

        final String[] featureNames = QaaDescriptor.createOutputFeatureNames(config);
        assertNotNull(featureNames);

        final String[] expected = {"a_pig_412", "a_pig_443", "a_pig_667", "a_ys_555", "bb_spm_510", "bb_spm_555", "bb_spm_667"};
        assertArrayEquals(expected, featureNames);
    }

    @Test
    public void testGetWavelengthInt() {
        double[] wavelengths = new double[]{12.9, 13.5, 14.3};

        assertEquals(13, QaaDescriptor.getWavelengthInt(wavelengths, 0));
        assertEquals(14, QaaDescriptor.getWavelengthInt(wavelengths, 1));
        assertEquals(14, QaaDescriptor.getWavelengthInt(wavelengths, 2));
    }

    @Test
    public void testGetWavelengthInt_errorCases() {
        double[] wavelengths = new double[]{13.9, 14.5, 15.3};

        try {
            QaaDescriptor.getWavelengthInt(wavelengths, -1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            QaaDescriptor.getWavelengthInt(wavelengths, 3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testValidateConfig_valid() {
        final QaaConfig config = createValidConfig();

        QaaDescriptor.validate(config);
    }

    @Test
    public void testValidateConfig_invalidSensorName() {
        final QaaConfig config = createValidConfig();
        config.setSensorName("Canon");

        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid sensor: Canon", expected.getMessage());
        }
    }

    @Test
    public void testValidateConfig_invalidNumberOfInputBands() {
        final QaaConfig config = createValidConfig();
        config.setBandNames(new String[] {"one", "two", "three", "four", "five"});

        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Illegal number of input bands: must provide six reflectance band names", expected.getMessage());
        }

        config.setBandNames(new String[] {"one", "two", "three", "four", "five", "six", "seven"});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Illegal number of input bands: must provide six reflectance band names", expected.getMessage());
        }
    }

    @Test
    public void testValidateConfig_invalidA_pig_out_indices() {
        final QaaConfig config = createValidConfig();
        config.setAPigOutIndices(new int[]{-1});

        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid a_pig_out index: -1. Must be in [0, 2]", expected.getMessage());
        }

        config.setAPigOutIndices(new int[]{3});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid a_pig_out index: 3. Must be in [0, 2]", expected.getMessage());
        }

        config.setAPigOutIndices(new int[]{0, 1, 0, 2});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid number of a_pig_out indices: 4. Must be in [0, 3]", expected.getMessage());
        }
    }

    @Test
    public void testValidateConfig_invalidA_total_out_indices() {
        final QaaConfig config = createValidConfig();

        config.setATotalOutIndices(new int[]{-1});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid a_total_out index: -1. Must be in [0, 4]", expected.getMessage());
        }

        config.setATotalOutIndices(new int[]{5});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid a_total_out index: 5. Must be in [0, 4]", expected.getMessage());
        }

        config.setATotalOutIndices(new int[]{0, 1, 0, 2, 3, 4});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid number of a_total_out indices: 6. Must be in [0, 5]", expected.getMessage());
        }
    }

    @Test
    public void testValidateConfig_invalidA_ys_out_indices() {
        final QaaConfig config = createValidConfig();
        config.setAYsOutIndices(new int[]{-1});

        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid a_ys_out index: -1. Must be in [0, 2]", expected.getMessage());
        }

        config.setAYsOutIndices(new int[]{3});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid a_ys_out index: 3. Must be in [0, 2]", expected.getMessage());
        }

        config.setAYsOutIndices(new int[]{0, 1, 0, 2});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid number of a_ys_out indices: 4. Must be in [0, 3]", expected.getMessage());
        }
    }

    @Test
    public void testValidateConfig_invalidBb_spm_out_indices() {
        final QaaConfig config = createValidConfig();

        config.setBbSpmOutIndices(new int[]{-1});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid bb_spm_out index: -1. Must be in [0, 4]", expected.getMessage());
        }

        config.setBbSpmOutIndices(new int[]{5});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid bb_spm_out index: 5. Must be in [0, 4]", expected.getMessage());
        }

        config.setBbSpmOutIndices(new int[]{0, 1, 0, 2, 3, 4});
        try {
            QaaDescriptor.validate(config);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid number of bb_spm_out indices: 6. Must be in [0, 5]", expected.getMessage());
        }
    }

    private QaaConfig createValidConfig() {
        final QaaConfig config = new QaaConfig();
        config.setSensorName(QaaConstants.MODIS);
        config.setBandNames(new String[] {"one", "two", "three", "four", "five", "six"});
        config.setAPigOutIndices(new int[]{0, 1, 2});
        config.setATotalOutIndices(new int[]{0, 1, 2, 3, 4});
        config.setAYsOutIndices(new int[]{0, 1, 2});
        config.setBbSpmOutIndices(new int[]{0, 1, 2, 3, 4});
        return config;
    }

    private VariableContext createValidContext() {
        final VariableContextImpl variableContext = new VariableContextImpl();
        variableContext.defineVariable("one");
        variableContext.defineVariable("two");
        variableContext.defineVariable("three");
        variableContext.defineVariable("four");
        variableContext.defineVariable("five");
        variableContext.defineVariable("six");
        return variableContext;
    }

}
