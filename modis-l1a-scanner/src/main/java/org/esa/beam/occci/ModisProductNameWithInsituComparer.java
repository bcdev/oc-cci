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

import com.vividsolutions.jts.geom.Geometry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModisProductNameWithInsituComparer {

    private static final long MAX_TIME_DIFFERENCE = 1000 * 60 * 60 * 3L; // Note: time in ms (NOT h)
    public static final DateFormat DEFAULT_DATE_FORMAT = DateUtils.createDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        String modisProductList = args[0];
        String insituCSV = args[1];

        List<Record> insituRecords = getInsituRecords(insituCSV);
        System.out.println("insituRecords.size() = " + insituRecords.size());
//        System.out.println("insituRecords.get(0) = " + insituRecords.get(0));
        List<Product> products = getModisProducts(modisProductList);
        System.out.println("modisProducts.size() = " + products.size());
//        System.out.println("modisProducts.get(0) = " + modisProducts.get(0).name);
        System.out.println();

        ProductInsituMatcher matcher = new ProductInsituMatcher(products, insituRecords, MAX_TIME_DIFFERENCE, false);
        matcher.match();
    }


    private static List<Product> getModisProducts(String filename) throws Exception {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        List<Product> modisNameProducts = new ArrayList<Product>();
        while (line != null) {
            modisNameProducts.add(new ModisNameProduct(line));
            line = bufferedReader.readLine();
        }
        return modisNameProducts;
    }

    private static List<Record> getInsituRecords(String filename) throws Exception {
        FileReader fileReader = new FileReader(filename);
        CsvRecordSource recordSource = new CsvRecordSource(fileReader, DEFAULT_DATE_FORMAT);
        List<Record> records = new ArrayList<Record>();
        for (Record record : recordSource.getRecords()) {
            records.add(record);
        }
        fileReader.close();
        return records;
    }

    private static class ModisNameProduct implements Product {
        private final String name;
        private final long startTime;
        private final long endTime;

        public ModisNameProduct(String name) throws Exception {
            this.name = name;
            Date startDateUTC = DateUtils.parse(name.substring(1, 14), "yyyyDDDHHmmss");
            startTime = startDateUTC.getTime();
            Calendar startCal = DateUtils.createCalendar();
            startCal.setTime(startDateUTC);
            startCal.add(Calendar.MINUTE, 5);
            endTime = startCal.getTimeInMillis();
        }

        @Override
        public long getEndTime() {
            return endTime;
        }

        @Override
        public long getStartTime() {
            return startTime;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Geometry getGeometry() {
            return null;
        }
    }
}
