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
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ModisProductWithInsituComparer {

    private static final long MAX_TIME_DIFFERENCE = 1000 * 60 * 60 * 3L; // Note: time in ms (NOT h)
    public static final DateFormat DEFAULT_INSITU_DATE_FORMAT = DateUtils.createDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat PRODUCT_DATE_FORMAT = DateUtils.createDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static void main(String[] args) throws Exception {
        String modisProductList = args[0];
        String insituCSV = args[1];

        List<SimpleRecord> insituRecords = getInsituRecords(insituCSV);
        System.out.println("insituRecords.size() = " + insituRecords.size());
//        System.out.println("insituRecords.get(0) = " + insituRecords.get(0));
        List<Product> products = getModisProducts(modisProductList);
        System.out.println("modisProducts.size() = " + products.size());
//        System.out.println("modisProducts.get(0) = " + products.get(0));
        System.out.println();

        ProductInsituMatcher matcher = new ProductInsituMatcher(products, insituRecords, MAX_TIME_DIFFERENCE, true);
        matcher.match();
    }


    private static List<Product> getModisProducts(String filename) throws Exception {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        List<Product> modisNameProducts = new ArrayList<Product>();
        while (line != null) {
            modisNameProducts.add(new ModisGeoProduct(line));
            line = bufferedReader.readLine();
        }
        return modisNameProducts;
    }

    private static List<SimpleRecord> getInsituRecords(String filename) throws Exception {
        FileReader fileReader = new FileReader(filename);
        CsvRecordSource recordSource = new CsvRecordSource(fileReader, DEFAULT_INSITU_DATE_FORMAT);
        List<SimpleRecord> records = new ArrayList<SimpleRecord>();
        for (Record record : recordSource.getRecords()) {
            records.add(new SimpleRecord(record.getTime().getTime(), record.getLocation()));
        }
        fileReader.close();
        return records;
    }

    private static class ModisGeoProduct implements Product {
        private static final WKTReader wktReader = new WKTReader();

        private final String name;
        private final long startTime;
        private final long endTime;
        private final String wkt;
        private Geometry geomtry;

        public ModisGeoProduct(String line) throws Exception {
            String[] splits = line.split("\t");
            this.name = splits[0];
            startTime = PRODUCT_DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[1])).getTime();
            endTime = PRODUCT_DATE_FORMAT.parse(DateUtils.getNoFractionString(splits[2])).getTime();
            wkt = splits[3];
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
            if (geomtry == null) {
                try {
                    geomtry = wktReader.read(wkt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return geomtry;
        }

        @Override
        public String toString() {
            return "ModisNameProduct{" +
                   "name='" + name + '\'' +
                   ", startTime=" + startTime +
                   ", endTime=" + endTime +
                   '}';
        }
    }


}
