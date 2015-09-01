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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by marcoz on 18.08.15.
 */
public class S2IndexCreatorMain {

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length != 2) {
            printUsage();
        }
        File productListFile = new File(args[0]);
        File indexfile = new File(args[1]);
        if (!productListFile.exists()) {
            System.err.printf("productList file '%s' does not exits%n", args[0]);
            printUsage();
        }
        int counter = 0;
        List<EoProduct> eoProductList = ProductDB.readProducts("s2", productListFile);
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexfile)))) {
            for (EoProduct eoProduct : eoProductList) {
                eoProduct.createGeo();
                S2EoProduct s2EoProduct = (S2EoProduct) eoProduct;
                dos.writeUTF(eoProduct.getName());
                dos.writeLong(eoProduct.getStartTime());
                dos.writeLong(eoProduct.getEndTime());

                s2EoProduct.writePolygone(dos);
                s2EoProduct.writeCellUnion(dos);

                counter++;
                if (counter % 10000 == 0) {
                    System.out.println("counter = " + counter);
                }
            }
        }
    }

    private static void printUsage() {
        System.err.println("Usage: S2IndexCreatorMain <productList> <indexfile>");
        System.exit(1);
    }

}
