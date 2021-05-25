package fun.bookish.peach.detector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 清晰度检测器
 */
public class SharpnessDetector {

    private float threshold = 10.0f;

    public SharpnessDetector() {
    }

    public SharpnessDetector(float threshold) {
        this.threshold = threshold;
    }

    private Result doDetect(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Mat laplaci = new Mat();
        Imgproc.Laplacian(gray, laplaci, CvType.CV_64F);
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble dev = new MatOfDouble();
        Core.meanStdDev(laplaci, mean, dev);
        double[] array = dev.toArray();
        double res = (array != null && array.length > 0) ? array[0] : -1;
        return new Result(threshold, (float) res, res < threshold);
    }

    public Result detect(Mat image) {
        return doDetect(image);
    }

    public Result detect(String imagePath) {
        Mat image = Imgcodecs.imread(imagePath);
        return doDetect(image);
    }

    public static class Result {
        private final float threshold;
        private final float result;
        private final boolean hasError;

        Result(float threshold, float result, boolean hasError) {
            this.threshold = threshold;
            this.result = result;
            this.hasError = hasError;
        }

        public boolean isHasError() {
            return hasError;
        }

        public float getThreshold() {
            return threshold;
        }

        public float getResult() {
            return result;
        }
    }

}
