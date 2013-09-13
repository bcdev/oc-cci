package org.esa.beam.occci.biascorrect;


import org.esa.beam.binning.operator.BinningConfig;
import org.esa.beam.binning.operator.BinningOp;
import org.esa.beam.binning.operator.FormatterConfig;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AcceptanceTestMain {

    public static void main(String[] args) throws IOException {
        registerBinningOp();

        final Product[] products = new Product[args.length];
        try {
            for (int i = 0; i < args.length; i++) {
                products[i] = ProductIO.readProduct(new File(args[i]));
            }

            GPF.createProduct("Binning", createDefaultParameterMap(), products);
        } finally {
            for (Product product : products) {
                if (product != null) {
                    product.dispose();
                }
            }
        }
    }

    private static void registerBinningOp() {
        final BinningOp.Spi spi = new BinningOp.Spi();
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(spi);
    }

    private static HashMap<String, Object> createDefaultParameterMap() {
        final HashMap<String, Object> parameterMap = new HashMap<String, Object>();

        final BinningConfig binningConfig = new BinningConfig();
        binningConfig.setNumRows(100);
        final AggregatorBiasCorrect.Config aggregatorConfig = new AggregatorBiasCorrect.Config();
        aggregatorConfig.varNames = new String[] {"radiance_1"};
        binningConfig.setAggregatorConfigs(aggregatorConfig);
        parameterMap.put("binningConfig", binningConfig);

        final FormatterConfig formatterConfig = new FormatterConfig();
        formatterConfig.setOutputFile(new File("delete_me").getAbsolutePath());
        parameterMap.put("formatterConfig", formatterConfig);
        return parameterMap;
    }
}
