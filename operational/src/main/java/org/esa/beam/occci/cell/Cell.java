package org.esa.beam.occci.cell;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.util.Date;

public interface Cell {

    float[] getFeatures();

    Point getCenter();
    Geometry getGeometry();

    Date getStartTime();
    Date getStopTime();
}
