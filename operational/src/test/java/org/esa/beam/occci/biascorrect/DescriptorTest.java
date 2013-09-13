package org.esa.beam.occci.biascorrect;

import org.esa.beam.binning.Aggregator;
import org.esa.beam.binning.AggregatorConfig;
import org.esa.beam.binning.AggregatorDescriptor;
import org.esa.beam.binning.VariableContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DescriptorTest {

    private AggregatorDescriptor descriptor;

    @Before
    public void setUp() {
        descriptor = new AggregatorBiasCorrect.Descriptor();
    }

    @Test
    public void testGetName() {
        assertEquals("OC-CCI-BIAS", descriptor.getName());
    }

    @Test
    public void testCreateConfig() {
        final AggregatorConfig config = descriptor.createConfig();
        assertNotNull(config);
        assertEquals("OC-CCI-BIAS", config.getName());
    }

    @Test
    public void testCreateAggregator() {
        final TestVariableContext context = new TestVariableContext();
        final AggregatorConfig config = descriptor.createConfig();

        final Aggregator aggregator = descriptor.createAggregator(context, config);
        assertNotNull(aggregator);
    }

    @Test
    public void testCreateAggregator_invalidConfigType() {
        final TestVariableContext context = new TestVariableContext();

        try {
            descriptor.createAggregator(context, new InvalidConfig());
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    private class TestVariableContext implements VariableContext {
        @Override
        public int getVariableCount() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int getVariableIndex(String name) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getVariableName(int index) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getVariableExpression(int index) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getValidMaskExpression() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private class InvalidConfig extends AggregatorConfig {
        @Override
        public String[] getVarNames() {
            return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}

