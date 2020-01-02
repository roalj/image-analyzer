package services;

import java.util.logging.Logger;

public class ImageAnalyzingRunnable implements Runnable{
    private Logger log = Logger.getLogger(ImageAnalyzingRunnable.class.getName());

    private Integer imageId;
    private AnalyzerBean analyzerBean;

    public ImageAnalyzingRunnable(Integer imageId) {
        this.imageId = imageId;
    }

    public void analyzeImage() {
        log.info("ANALIZIRAMO SLIKO!!! IMAGE-ANLYZER");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        analyzeImage();
    }
}
