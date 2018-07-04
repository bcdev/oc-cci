/*
 * Copyright (C) 2017 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.beam.occci.processing;

import org.esa.beam.binning.VariableContext;

/**
 * A simple VariableContext.
 *
 * @author Norman
 */
public class MyVariableContext implements VariableContext {
    private String[] varNames;

    public MyVariableContext(String... varNames) {
        this.varNames = varNames;
    }

    @Override
    public int getVariableCount() {
        return varNames.length;
    }

    @Override
    public String getVariableName(int i) {
        return varNames[i];
    }

    @Override
    public int getVariableIndex(String name) {
        for (int i = 0; i < varNames.length; i++) {
            if (name.equals(varNames[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String getVariableExpression(int i) {
        return null;
    }

    @Override
    public String getVariableValidExpression(int index) {
        return null;
    }


    @Override
    public String getValidMaskExpression() {
        return null;
    }
}
