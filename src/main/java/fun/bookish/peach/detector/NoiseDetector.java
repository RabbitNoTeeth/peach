package fun.bookish.peach.detector;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

/**
 * 噪声检测器
 */
public class NoiseDetector {

    private float threshold = 200.0f;

    public NoiseDetector() {
    }

    public NoiseDetector(float threshold) {
        this.threshold = threshold;
    }

    private Mat convmxtH(Mat H) {
        int m = 7;
        int n = 7;
        int s1 = H.rows();
        int s2 = H.cols();
        int II = m - s1 + 1;
        int JJ = n - s2 + 1;
        int Tw = II * JJ;
        int Th = m * n;
        Mat T = Mat.zeros(Tw, Th, CvType.CV_64F);
        int k = 1;
        int t;
        for (int i = 1; i < II + 1; i++) {
            for (int j = 1; j < JJ + 1; j++) {
                for (int p = 1; p < s2 + 1; p++) {
                    t = (i - 1) * n + (j - 1) + p;
                    T.put(k - 1, t - 1, H.get(0, p - 1));
                }
                k = k + 1;
            }
        }
        return T;
    }

    private Mat convmxtV(Mat H) {
        int m = 7;
        int n = 7;
        int s1 = H.rows();
        int s2 = H.cols();
        int II = m - s1 + 1;
        int JJ = n - s2 + 1;
        int Tw = II * JJ;
        int Th = m * n;
        Mat T = Mat.zeros(Tw, Th, CvType.CV_64F);
        int k = 1;
        int h;
        for (int i = 1; i < II + 1; i++) {
            for (int j = 1; j < JJ + 1; j++) {
                for (int p = 1; p < s1 + 1; p++) {
                    h = (i - 1 + p - 1) * n + (j - 1) + 1 + s2 - 1;
                    T.put(k - 1, h - 1, H.get(p - 1, 0));
                }
                k = k + 1;
            }
        }
        return T;
    }

    private Mat im2col(Mat im, Mat patchsize) {
        int ma = im.rows();
        int na = im.cols();
        int m = (int) patchsize.get(0, 0)[0];
        int n = (int) patchsize.get(0, 1)[0];
        int mc = m;
        int nc = ma - m + 1;
        int nn = na - n + 1;
        Mat cidx = new Mat(mc, nc, CvType.CV_64F);
        Mat ridx = new Mat(mc, nc, CvType.CV_64F);
        Mat t = new Mat(mc, nc, CvType.CV_64F);
        for (int i = 0; i < nc; i++) {
            for (int j = 0; j < mc; j++) {
                cidx.put(j, i, j);
            }
        }
        for (int i = 0; i < mc; i++) {
            for (int j = 0; j < nc; j++) {
                ridx.put(i, j, j + 1);
            }
        }
        Core.add(cidx, ridx, t);
        Mat hh = new Mat(mc, nc, CvType.CV_64F);
        Mat tt = Mat.zeros(mc * n, nc, CvType.CV_64F);
        int aa, bb;
        for (int i = 0; i < n; i++) {
            Core.add(t, new Scalar(ma * i), hh);
            aa = i * mc;
            bb = i * mc + mc;
            hh.copyTo(tt.rowRange(aa, bb));
        }
        Mat ttt = Mat.zeros(mc * n, nc * nn, CvType.CV_64F);
        Mat gg = new Mat(mc * n, nc, CvType.CV_64F);
        int cc, dd;
        for (int j = 0; j < nn; j++) {
            Core.add(tt, new Scalar(ma * j), gg);
            cc = j * nc;
            dd = j * nc + nc;
            gg.copyTo(ttt.colRange(cc, dd));
        }
        return matchange(im, ttt);
    }

    private Mat matchange(Mat A, Mat B) {
        int n1 = B.rows();
        int n2 = B.cols();
        Mat C = new Mat(n1, n2, CvType.CV_64F);
        // todo 数据转换
        return C;
    }

    private Result doDetect(Mat image) {
        boolean hasError = false;
        float res = -1f;
        Imgproc.resize(image, image, new Size(), 0.3, 0.3);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        image.convertTo(image, CvType.CV_64F);
        Mat img2 = new Mat();
        Core.divide(image, new Scalar(255), img2);
        Mat imgh0 = new Mat();
        Mat imgv0 = new Mat();
        Mat imgh1 = new Mat();
        Mat imgv1 = new Mat();
        Mat imgh;
        Mat imgv;
        Mat imgh2 = new Mat();
        Mat imgv2 = new Mat();
        Mat kh = new Mat(1, 3, CvType.CV_64F, new Scalar(-0.5, 0, 0.5));
        Imgproc.filter2D(image, imgh0, image.depth(), kh);
        imgh0.colRange(1, imgh0.cols() - 1).copyTo(imgh1);
        imgh = imgh1.mul(imgh1);
        Core.divide(imgh, new Scalar(255), imgh2);
        Mat kv = new Mat(3, 1, CvType.CV_64F, new Scalar(-0.5, 0, 0.5));
        Imgproc.filter2D(image, imgv0, image.depth(), kv);
        imgv0.rowRange(1, imgv0.rows() - 1).copyTo(imgv1);
        imgv = imgv1.mul(imgv1);
        Core.divide(imgv, new Scalar(255), imgv2);
        Mat Dh = convmxtH(kh);
        Mat Dh_t = Dh.t();
        Mat DHT = new Mat();
        Core.gemm(Dh_t, Dh, 1, new Mat(), 0, DHT);
        Mat Dv = convmxtV(kv);
        Mat Dv_t = Dv.t();
        Mat DVT = new Mat();
        Core.gemm(Dv_t, Dv, 1, new Mat(), 0, DVT);
        Mat DD = new Mat();
        Core.add(DHT, DVT, DD);
        double tau0 = 81.8208;
        Mat patchsize = new Mat(1, 2, CvType.CV_32S, new Scalar(7, 7));
        Mat patchsize_h = new Mat(1, 2, CvType.CV_32S, new Scalar(7, 5));
        Mat patchsize_v = new Mat(1, 2, CvType.CV_32S, new Scalar(5, 7));
        Mat X, Xh, Xv;
        X = im2col(image, patchsize);
        Xh = im2col(imgh, patchsize_h);
        Xv = im2col(imgv, patchsize_v);
        Mat Xt = new Mat(Xh.rows() * 2, Xh.cols(), CvType.CV_64F);
        Xh.copyTo(Xt.rowRange(0, Xt.rows() / 2));
        Xv.copyTo(Xt.rowRange(Xt.rows() / 2, Xt.rows()));
        Mat Xtr = new Mat(1, Xt.cols(), CvType.CV_64F);
        Core.reduce(Xt, Xtr, 0, Core.REDUCE_SUM);
        Mat temp = new Mat();
        Core.gemm(X, X.t(), 1, new Mat(), 0, temp);
        Mat cov = new Mat();
        Core.divide(temp, new Scalar(X.cols() - 1), cov);
        Mat eValuesMat = new Mat();
        Mat eVectorsMat = new Mat();
        Core.eigen(cov, eValuesMat, eVectorsMat);
        double sig2 = eValuesMat.get(eValuesMat.rows() - 1, 0)[0];
        double tau;
        Mat cov2 = new Mat();
        Mat eVal2 = new Mat();
        Mat eVec2 = new Mat();
        Mat Xtr2 = Xtr.clone();
        Mat X2 = X.clone();
        for (int k = 0; k < 1; k++) {
            tau = sig2 * tau0;
            Mat Xtrout = new Mat();
            Mat Xout = new Mat(X.clone().size(), CvType.CV_64F);
            double gg;
            Mat hh;
            int w = 0;
            for (int i = 0; i < Xtr2.cols(); i++) {
                gg = Xtr2.get(0, i)[0];
                hh = X2.col(i);
                if (gg < tau) {
                    w = w + 1;
                    Xtrout.push_back(new MatOfDouble(gg));
                    hh.copyTo(Xout.colRange(w - 1, w));
                }
            }
            Xtrout = Xtrout.t();
            Xout = Xout.colRange(0, w);
            Mat temp2 = new Mat();
            Core.multiply(Xout, Xout.t(), temp2);
            Core.divide(temp2, new Scalar(Xout.cols() - 1), cov2);
            Core.eigen(cov2, eVal2, eVec2);
            sig2 = eVal2.get(eVal2.rows() - 1, 0)[0];
            if (sig2 > threshold) {
                res = (float) sig2;
                hasError = true;
//                break;
            }
            Xtr2 = Xtrout.clone();
            X2 = Xout.clone();
        }

        return new Result(threshold, res, hasError);
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
