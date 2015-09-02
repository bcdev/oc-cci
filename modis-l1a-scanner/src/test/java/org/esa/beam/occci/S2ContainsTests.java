package org.esa.beam.occci;

import com.google.common.geometry.S2Cell;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2CellUnion;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import com.google.common.geometry.S2RegionCoverer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class S2ContainsTests {

    String point = "POINT(-152.314453125 -61.25332962507908)";
    String poly4 = "POLYGON((-131.751375475231 -56.7904943186878,-174.331351290875 -62.8620857336271,-174.36308104874 -44.6401395594105,-144.988449544122 -40.696164889468,-131.751375475231 -56.7904943186878))";

    @Test
    public void testConstains4Points() {
        S2Polygon polygon = S2EoProduct.createPolygon(poly4);
        S2Point s2Point = S2LatLng.fromDegrees(-61.25332962507908, -152.314453125).toPoint();
        boolean contains = polygon.contains(s2Point);
        assertTrue(contains);
    }

    @Test
    public void testCover() {
        S2Polygon polygon = S2EoProduct.createPolygon(poly4);
        S2Point s2Point = S2LatLng.fromDegrees(-61.25332962507908, -152.314453125).toPoint();

        S2RegionCoverer coverer = new S2RegionCoverer();
        coverer.setMinLevel(0);
        coverer.setMaxLevel(3);
        coverer.setMaxCells(500);
        S2CellUnion covering = coverer.getCovering(polygon);

        final GeometryFactory factory = new GeometryFactory();
        List<S2CellId> s2CellIds = covering.cellIds();
        System.out.println("s2CellIds.size() = " + s2CellIds.size());
        List<Polygon> polys = new ArrayList<>();

        for (S2CellId s2CellId : s2CellIds) {
            ArrayList<double[]> coordList = new ArrayList<double[]>();

            //System.out.println("s2CellId = " + s2CellId);
            S2Cell s2Cell = new S2Cell(s2CellId);
            //System.out.println("s2Cell = " + s2Cell);
            S2LatLng s2LatLng = new S2LatLng(s2Cell.getVertex(0));
            coordList.add(new double[]{s2LatLng.lat().degrees(), s2LatLng.lng().degrees()});

            s2LatLng = new S2LatLng(s2Cell.getVertex(1));
            coordList.add(new double[]{s2LatLng.lat().degrees(), s2LatLng.lng().degrees()});

            s2LatLng = new S2LatLng(s2Cell.getVertex(2));
            coordList.add(new double[]{s2LatLng.lat().degrees(), s2LatLng.lng().degrees()});

            s2LatLng = new S2LatLng(s2Cell.getVertex(3));
            coordList.add(new double[]{s2LatLng.lat().degrees(), s2LatLng.lng().degrees()});

            s2LatLng = new S2LatLng(s2Cell.getVertex(0));
            coordList.add(new double[]{s2LatLng.lat().degrees(), s2LatLng.lng().degrees()});

            final Coordinate[] coordinates = new Coordinate[coordList.size()];
            for (int i1 = 0; i1 < coordinates.length; i1++) {
                final double[] coord = coordList.get(i1);
                coordinates[i1] = new Coordinate(coord[1], coord[0]);
            }
            Polygon p = factory.createPolygon(factory.createLinearRing(coordinates), null);
            //System.out.println(p);
            polys.add(p);
        }
        System.out.println("polys.size() = " + polys.size());
        MultiPolygon multiPolygon = factory.createMultiPolygon(polys.toArray(new Polygon[polys.size()]));
        System.out.println(multiPolygon);

    }
}
