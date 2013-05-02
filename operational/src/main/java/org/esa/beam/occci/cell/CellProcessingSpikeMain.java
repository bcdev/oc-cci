package org.esa.beam.occci.cell;


import java.io.File;
import java.io.IOException;

public class CellProcessingSpikeMain {

    public static void main(String[] args) throws IOException {
        final File file = new File(args[0]);

        final BinnedCellReader reader = new BinnedCellReader();
        reader.open(file);

        reader.close();
    }
}
