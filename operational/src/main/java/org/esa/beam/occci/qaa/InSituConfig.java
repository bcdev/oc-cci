package org.esa.beam.occci.qaa;


public class InSituConfig implements SensorConfig {

    private static final double[] awCoefficients = {-1.146, -1.366, -0.469};

    @Override
    public double[] getAwCoefficients() {
        return awCoefficients;
    }

    @Override
    public double getReferenceWavelength() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double[] getWavelengths() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double[] getSpecificAbsorptions() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double[] getSpecficBackscatters() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
