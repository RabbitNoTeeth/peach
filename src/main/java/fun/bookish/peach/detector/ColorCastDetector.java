package fun.bookish.peach.detector;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;

/**
 * 色偏检测器
 */
public class ColorCastDetector {

    private float threshold = 2f;

    public ColorCastDetector() {
    }

    public ColorCastDetector(float threshold) {
        this.threshold = threshold;
    }

    private Result doDetect(Mat image) {
        Mat lab = new Mat();
        opencv_imgproc.cvtColor(image, lab, opencv_imgproc.COLOR_BGR2Lab);
        MatVector labChannels = new MatVector();
        opencv_core.split(lab, labChannels);
        Mat channel_l = labChannels.get(0);
        Mat channel_a = labChannels.get(1);
        Mat channel_b = labChannels.get(2);
        int h = lab.rows();
        int w = lab.cols();
        // da > 0,偏红,否则偏绿
        double da = opencv_core.sumElems(channel_a).get() / (h * w) - 128;
        // db > 0,偏黄,否则偏蓝
        double db = opencv_core.sumElems(channel_b).get() / (h * w) - 128;
        int[] hist_a = new int[256];
        int[] hist_b = new int[256];
        UByteRawIndexer aIndexer = channel_a.createIndexer();
        UByteRawIndexer bIndexer = channel_b.createIndexer();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int ta = aIndexer.get(i, j);
                int tb = bIndexer.get(i, j);
                hist_a[ta] += 1;
                hist_b[tb] += 1;
            }
        }
        double msq_a = 0f;
        double msq_b = 0f;
        for (int i = 0; i < 256; i++) {
            msq_a += Math.abs(i - 128 - da) * hist_a[i] / (w * h);
            msq_b += Math.abs(i - 128 - db) * hist_b[i] / (w * h);
        }
        double dividend = Math.sqrt(da * da + db * db);
        double divisor = Math.sqrt(msq_a * msq_a + msq_b * msq_b);
        double res = 0f;
        boolean hasError = false;
        ErrorType errorType = ErrorType.NONE;
        if (dividend != 0d) {
            if (divisor != 0d) {
                // 偏色因子越大，偏色越严重
                res = dividend / divisor;
                hasError = res > threshold;
                errorType = decideColorType(da, db);
            }
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("da=" + da + ", db=" + db + ", res=" + res);
        return new ColorCastDetector.Result(threshold, res, hasError, errorType);
    }

    private ErrorType decideColorType(double da, double db) {
        // todo 根据da、db值判断具体偏色情况（红、黄、蓝、绿）
        return null;
    }

    public Result detect(Mat image) {
        return doDetect(image);
    }

    public Result detect(String imagePath) {
        Mat image = opencv_imgcodecs.imread(imagePath);
        return doDetect(image);
    }

    public static class Result {
        private final double threshold;
        private final double result;
        private final boolean hasError;
        private final ErrorType errorType;

        Result(double threshold, double result, boolean hasError, ErrorType errorType) {
            this.threshold = threshold;
            this.result = result;
            this.hasError = hasError;
            this.errorType = errorType;
        }

        public boolean isHasError() {
            return hasError;
        }

        public double getThreshold() {
            return threshold;
        }

        public double getResult() {
            return result;
        }
    }

    public enum ErrorType {
        NONE,
        TOO_RED,
        TOO_GREEN,
        TOO_YELLOW,
        TOO_BLUE;
    }

}
