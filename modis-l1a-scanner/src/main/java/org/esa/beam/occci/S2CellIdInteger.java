/*
 * Copyright (C) 2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.occci;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;

import java.util.Arrays;

/**
 * Created by marcoz on 06.11.15.
 */
public class S2CellIdInteger {

    public static int asInt(S2CellId s2CellId) {
        if (s2CellId.level() > 13) {
            s2CellId = s2CellId.parent(13);
        }
        return (int) (s2CellId.id() >>> 34);
    }

    public static strictfp boolean containsPoint(final int[] intCellIds, final S2CellId s2CellId) {
        int id = asInt(s2CellId);
        int pos = Arrays.binarySearch(intCellIds, id);
        if (pos < 0) {
            pos = -pos - 1;
        }

        return pos < intCellIds.length && rangeMin(intCellIds[pos]) <= id || pos != 0 && rangeMax(intCellIds[pos - 1]) >= id;
    }

    public static int rangeMin(int s2cell) {
        return s2cell - (lowestOnBit(s2cell) - 1);
    }

    public static int rangeMax(int s2cell) {
        return s2cell + (lowestOnBit(s2cell) - 1);
    }

    public static int lowestOnBit(int s2cell) {
        return s2cell & -s2cell;
      }

    public static void main(String[] args) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(42, 10);
        System.out.println("s2LatLng = " + s2LatLng);
        S2CellId s2CellId = S2CellId.fromLatLng(s2LatLng);
        System.out.println("s2CellId = " + s2CellId);
        S2CellId s2CellId13 = s2CellId.parent(13);
        System.out.println("s2CellId13 = " + s2CellId13);

        System.out.println("s2cellId = " + Long.toBinaryString(s2CellId.id()));
        System.out.println("s2cellId13 = " + Long.toBinaryString(s2CellId13.id()));
        System.out.println("s2cellIdInt = " + Long.toBinaryString(asInt(s2CellId)));
    }
}
