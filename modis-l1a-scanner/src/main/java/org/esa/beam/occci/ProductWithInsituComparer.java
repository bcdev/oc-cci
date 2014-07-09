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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductWithInsituComparer {

    private static final long HOURS_IN_MILLIS = 1000 * 60 * 60; // Note: time in ms (NOT h)
    //    private static final long MAX_TIME_DIFFERENCE = 1000 * 60 * 60 * 3L; // Note: time in ms (NOT h)
    public static final DateFormat DEFAULT_INSITU_DATE_FORMAT = DateUtils.createDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat PRODUCT_DATE_FORMAT = DateUtils.createDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            printUsage();
        }
        File productListFile = new File(args[0]);
        if (!productListFile.exists()) {
            System.err.printf("productList file '%s' does not exits%n", args[0]);
            printUsage();
        }
        File insituCSVtFile = new File(args[1]);
        if (!insituCSVtFile.exists()) {
            System.err.printf("insituList file '%s' does not exits%n", args[1]);
            printUsage();
        }
        int hours = 0;
        try {
            hours = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.printf("cannot parse hours '%s' %n", args[2]);
            printUsage();
        }

        List<SimpleRecord> insituRecords = getInsituRecords(insituCSVtFile);
        System.err.println("num insituRecords = " + insituRecords.size());
        List<Product> products = getModisProducts(productListFile);
        System.err.println("num products =  " + products.size());
        System.err.println();

        ProductInsituMatcher matcher = new ProductInsituMatcher(products, insituRecords, hours * HOURS_IN_MILLIS, true);
        matcher.match();
    }

    private static void printUsage() {
        System.err.println("Usage: ProductWithInsituComparer <productList> <insituList> <hours>");
        System.exit(1);
    }


    private static List<Product> getModisProducts(File file) throws IOException, java.text.ParseException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        List<Product> modisNameProducts = new ArrayList<Product>();
        while (line != null) {
            modisNameProducts.add(new ModisGeoProduct(line));
            line = bufferedReader.readLine();
        }
        return modisNameProducts;
    }

    private static List<SimpleRecord> getInsituRecords(File file) throws Exception {
        FileReader fileReader = new FileReader(file);
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

        public ModisGeoProduct(String line) throws java.text.ParseException {
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
