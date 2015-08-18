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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by marcoz on 17.08.15.
 */
public class ProductDB {

    private static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24L;

    private final List<EoProduct> eoProducts;
    private final int[] dayIndex;
    private final int firstDay;
    private final int lastDay;

    public ProductDB(List<EoProduct> eoProducts, int[] dayIndex, int firstDay, int lastDay) {
        this.eoProducts = eoProducts;
        this.dayIndex = dayIndex;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
    }

    static List<EoProduct> readProducts(String format, File file) throws IOException, java.text.ParseException {
        long t1 = System.currentTimeMillis();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        List<EoProduct> eoProducts = new ArrayList<EoProduct>();
        while (line != null) {
            EoProduct eoProduct = createEoProduct(format, line);
            if (eoProduct != null) {
                eoProducts.add(eoProduct);
            }
            line = bufferedReader.readLine();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("read products time (" + format + ") = " + ((t2 - t1) / 1000f));
        return eoProducts;
    }

    private static EoProduct createEoProduct(String format, String line) throws ParseException {
        if ("jts".equals(format)) {
            return JtsEoProduct.parse(line);
        } else if ("spatial3d".equals(format)) {
            return Spatial3dEoProduct.parse(line);
        } else if ("s2".equals(format)) {
            return S2EoProduct.parse(line);
        } else if ("s2i".equals(format)) {
            return S2IEoProduct.parse(line);
        }
        throw new IllegalArgumentException("Unknow format " + format);
    }

    static ProductDB create(List<EoProduct> eoProducts) {
        Collections.sort(eoProducts, new Comparator<EoProduct>() {
            @Override
            public int compare(EoProduct eo1, EoProduct eo2) {
                final long t1 = eo1.getStartTime();
                final long t2 = eo2.getStartTime();
                return (t1 < t2 ? -1 : (t1 == t2 ? 0 : 1));
            }
        });

        EoProduct eoFirst = eoProducts.get(0);
        EoProduct eoLast = eoProducts.get(eoProducts.size() - 1);

        int firstDay = getDaySinceEpoch(eoFirst.getStartTime());
        int lastDay = getDaySinceEpoch(eoLast.getEndTime());
        int indexLength = lastDay - firstDay + 1;
        int[] dayIndex = new int[indexLength];
        Arrays.fill(dayIndex, -1);
        for (int i = 0; i < eoProducts.size(); i++) {
            EoProduct eoProduct = eoProducts.get(i);

            int daysSinceEpoch = getDaySinceEpoch(eoProduct.getStartTime());
            int indexID = daysSinceEpoch - firstDay;
            if (dayIndex[indexID] == -1) {
                dayIndex[indexID] = i;
            }
        }

        return new ProductDB(eoProducts, dayIndex, firstDay, lastDay);
    }

    public static int getDaySinceEpoch(long time) {
        return (int) (time / MILLIS_PER_DAY);
    }

    public int getIndexForTime(long startTime) {
        int daysSinceEpoch = getDaySinceEpoch(startTime);
        if (daysSinceEpoch < firstDay || daysSinceEpoch > lastDay) {
            return -1;
        }
        return dayIndex[(daysSinceEpoch - firstDay)];
    }

    public EoProduct getRecord(int index) {
        if (index < eoProducts.size()) {
            return eoProducts.get(index);
        } else {
            return null;
        }
    }

    public int size() {
        return eoProducts.size();
    }

    public List<EoProduct> list() {
        return eoProducts;
    }
}
