/*
 * Copyright (C) 2016 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Gets all products at 'Gustav-Dahlen-Tower'
 */
public class GDTMain {

    public static void main(String[] args) throws IOException, ParseException {
        List<EoProduct> eoProductList = ProductDB.readProducts("s2", new File(args[0]));
        System.out.println("eoProductList = " + eoProductList.size());
        int count = 0;
        int error = 0;
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2005, 06, 01);
        long startDate = gregorianCalendar.getTimeInMillis();

        for (EoProduct eoProduct : eoProductList) {
            try {
                boolean contains = eoProduct.contains(17.46683, 58.59417);
                if (contains) {
                    if (eoProduct.getStartTime() > startDate) {
                        System.out.println("eoProduct = " + eoProduct.getName());
                        count++;
                    }
                }
            } catch (Throwable t) {
//                System.err.println("eoProduct = " + eoProduct.getName());
                error++;
            }
        }
        System.out.println("count = " + count);
        System.out.println("error = " + error);
    }
}
