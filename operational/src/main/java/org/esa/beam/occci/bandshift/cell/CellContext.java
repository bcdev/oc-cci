package org.esa.beam.occci.bandshift.cell;


import java.util.ArrayList;

public class CellContext {

    private final ArrayList<String> featureNames;

    public CellContext() {
        featureNames = new ArrayList<String>();
    }

    public int getFeatureCount() {
        return featureNames.size();
    }

    public void addFeature(String featureName) {
       featureNames.add(featureName);
    }
}
