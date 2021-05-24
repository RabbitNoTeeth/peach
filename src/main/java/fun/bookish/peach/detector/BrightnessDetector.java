package fun.bookish.peach.detector;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BrightnessDetector {

    private float threshold1 = 1f;
    private float threshold2 = 0f;
    private int factor = 128;

    public BrightnessDetector() {
    }

    public BrightnessDetector(float threshold1, float threshold2) {
        this.threshold1 = threshold1;
        this.threshold2 = threshold2;
    }

    public BrightnessDetector(float threshold1, float threshold2, int factor) {
        this(threshold1, threshold2);
        this.factor = factor;
    }

    private float[] calculate(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        float a = 0f;
        int[] hist = new int[256];
        for (int i = 0; i < 256; i++)
            hist[i] = 0;
        for (int i = 0; i < gray.rows(); i++) {
            for (int j = 0; j < gray.cols(); j++) {
                int x = (int) gray.get(i, j)[0];
                a += (float) (x - factor);//在计算过程中，考虑128为亮度均值点，统计偏离的总数
                hist[x]++; //统计每个亮度的次数
            }
        }
        float da = a / (float) (gray.rows() * gray.cols());
        float D = Math.abs(da);
        float Ma = 0;
        for (int i = 0; i < 256; i++) {
            Ma += Math.abs(i - factor - da) * hist[i];
        }
        Ma /= (float) (gray.rows() * gray.cols());
        float M = Math.abs(Ma);
        float K = D / M;
        float[] res = {K, da};
        return res;
    }

    private Result doDetect(Mat image) {
        float[] calculate = calculate(image);
        boolean hasError = false;
        int errorCode = 0;
        if (calculate[0] > threshold1) {
            hasError = true;
            errorCode = calculate[1] > threshold2 ? 1 : -1;
        }
        return new Result(threshold1, threshold2, calculate[0], calculate[1], hasError, errorCode);
    }

    public Result detect(Mat image) {
        return doDetect(image);
    }

    public Result detect(String imagePath) {
        Mat image = Imgcodecs.imread(imagePath);
        return doDetect(image);
    }

    public static class Result {
        private final float threshold1;
        private final float threshold2;
        private final float result1;
        private final float result2;
        private final boolean hasError;
        private final int errorCode;

        Result(float threshold1, float threshold2, float result1, float result2, boolean hasError, int errorCode) {
            this.threshold1 = threshold1;
            this.threshold2 = threshold2;
            this.result1 = result1;
            this.result2 = result2;
            this.hasError = hasError;
            this.errorCode = errorCode;
        }

        public float getResult1() {
            return result1;
        }

        public float getResult2() {
            return result2;
        }

        public float getThreshold1() {
            return threshold1;
        }

        public float getThreshold2() {
            return threshold2;
        }

        public boolean isHasError() {
            return hasError;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

}
