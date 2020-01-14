package services;

import javax.enterprise.inject.spi.CDI;
import java.util.logging.Logger;

public class ImageAnalyzingRunnable implements Runnable {
    private Logger log = Logger.getLogger(ImageAnalyzingRunnable.class.getName());

    private Integer imageId;

    private AnalyzerBean analyzerBean;

    private void analyze() {
        log.info("ANALIZIRAMO SLIKO!!! IMAGE-ANLYZER");
        analyzerBean = CDI.current().select(AnalyzerBean.class).get();
        log.info(imageId + "");
        analyzerBean.analyze(imageId);
    }

    @Override
    public void run() {
        analyze();
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
