package fun.bookish.peach.detector;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.*;

/**
 * 条纹检测器
 */
public class StripeDetector {

    private float threshold = 0.008f;

    public StripeDetector() {
    }

    public StripeDetector(float threshold) {
        this.threshold = threshold;
    }

    private Result doDetect(Mat image) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
        List<Mat> hsvChannels = new ArrayList<>();
        Core.split(hsv, hsvChannels);
        Mat channelH = hsvChannels.get(0);
        int m = Core.getOptimalDFTSize(channelH.rows());
        int n = Core.getOptimalDFTSize(channelH.cols());
        Core.copyMakeBorder(channelH, channelH, 0, m - channelH.rows(), 0, n - channelH.cols(), Core.BORDER_CONSTANT, new Scalar(0));
        Mat mFourier = new Mat(channelH.rows() + m, channelH.cols() + n, CvType.CV_32FC2, new Scalar(0, 0));
        List<Mat> mForFourier = new ArrayList<>();
        Mat m1 = new Mat();
        channelH.convertTo(m1, CvType.CV_32F);
        mForFourier.add(m1);
        mForFourier.add(Mat.zeros(channelH.size(), CvType.CV_32F));
        Mat mSrc = Mat.zeros(channelH.size(), CvType.CV_32F);
        merge(mForFourier, mSrc);
        Core.dft(mSrc, mFourier);
        List<Mat> channels = new ArrayList<>();
        Core.split(mFourier, channels);
        Mat mRe = channels.get(0);
        Mat mIm = channels.get(1);
        Mat mAmplitude = new Mat();
        Core.magnitude(mRe, mIm, mAmplitude);
        add(mAmplitude, new Scalar(1), mAmplitude);
        log(mAmplitude, mAmplitude);
        MatOfDouble means = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(mAmplitude, means, stddev);
        double men = means.toArray()[0];
        double std = stddev.toArray()[0];
        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(mAmplitude);
        double max_v = minMaxLocResult.maxVal;
        double min_v = minMaxLocResult.minVal;
        double T = Math.max(men + 3 * std, max_v / 2);
        double count = 0;
        int height = mAmplitude.rows();
        int width = mAmplitude.cols();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mAmplitude.get(i, j)[0] > T) {
                    count++;
                }
            }
        }
        double res = count / (height * width);
        return new Result(threshold, (float) res, res > threshold);
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
