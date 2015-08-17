/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.util.Date;

public class SimpleRecord {

    static final DateFormat INSITU_DATE_FORMAT = DateUtils.createDateFormat("yyyy-MM-dd HH:mm:ss");

    private final long time;
    private final Point2D.Float location;

    public SimpleRecord(long time, Point2D.Float location) {
        this.time = time;
        this.location = location;
    }

    long getTime() {
        return time;
    }

    public Point2D.Float getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "SimpleRecord{" +
                "time=" + INSITU_DATE_FORMAT.format(new Date(time)) +
                ", location=" + location +
                '}';
    }
}
