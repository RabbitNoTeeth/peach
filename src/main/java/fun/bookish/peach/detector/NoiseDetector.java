package fun.bookish.peach.detector;

import jdk.nashorn.internal.ir.CallNode;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.indexer.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import java.nio.Buffer;
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
        Mat T = Mat.zeros(Tw, Th, opencv_core.CV_64F).asMat();
        DoubleRawIndexer tIndexer = T.createIndexer();
        DoubleRawIndexer hIndexer = H.createIndexer();
        int k = 1;
        int t;
        for (int i = 1; i < II + 1; i++) {
            for (int j = 1; j < JJ + 1; j++) {
                for (int p = 1; p < s2 + 1; p++) {
                    t = (i - 1) * n + (j - 1) + p;
                    tIndexer.put(k - 1, t - 1, hIndexer.get(0, p - 1));
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
        Mat T = Mat.zeros(Tw, Th, opencv_core.CV_64F).asMat();
        DoubleRawIndexer tIndexer = T.createIndexer();
        DoubleRawIndexer hIndexer = H.createIndexer();
        int k = 1;
        int h;
        for (int i = 1; i < II + 1; i++) {
            for (int j = 1; j < JJ + 1; j++) {
                for (int p = 1; p < s1 + 1; p++) {
                    h = (i - 1 + p - 1) * n + (j - 1) + 1 + s2 - 1;
                    tIndexer.put(k - 1, h - 1, hIndexer.get(p - 1, 0));
                }
                k = k + 1;
            }
        }
        return T;
    }

    private Mat im2col(Mat im, Mat patchsize) {
        int ma = im.rows();
        int na = im.cols();
        IntRawIndexer patchsizeIndexer = patchsize.createIndexer();
        int m = patchsizeIndexer.get(0, 0);
        int n = patchsizeIndexer.get(0, 1);
        int mc = m;
        int nc = ma - m + 1;
        int nn = na - n + 1;
        Mat cidx = new Mat(mc, nc, opencv_core.CV_64F);
        DoubleRawIndexer cidxIndexer = cidx.createIndexer();
        Mat ridx = new Mat(mc, nc, opencv_core.CV_64F);
        DoubleRawIndexer ridxIndexer = ridx.createIndexer();
        Mat t = new Mat(mc, nc, opencv_core.CV_64F);
        for (int i = 0; i < nc; i++) {
            for (int j = 0; j < mc; j++) {
                cidxIndexer.put(j, i, j);
            }
        }
        for (int i = 0; i < mc; i++) {
            for (int j = 0; j < nc; j++) {
                ridxIndexer.put(i, j, j + 1);
            }
        }
        opencv_core.add(cidx, ridx, t);
        Mat hh = new Mat(mc, nc, opencv_core.CV_64F);
        Mat tt = Mat.zeros(mc * n, nc, opencv_core.CV_64F).asMat();
        int aa, bb;
        for (int i = 0; i < n; i++) {
            hh = opencv_core.add(t, new Scalar(ma * i)).asMat();
            aa = i * mc;
            bb = i * mc + mc;
            hh.copyTo(tt.rowRange(aa, bb));
        }
        Mat ttt = Mat.zeros(mc * n, nc * nn, opencv_core.CV_64F).asMat();
        Mat gg = new Mat(mc * n, nc, opencv_core.CV_64F);
        int cc, dd;
        for (int j = 0; j < nn; j++) {
            gg = opencv_core.add(tt, new Scalar(ma * j)).asMat();
            cc = j * nc;
            dd = j * nc + nc;
            gg.copyTo(ttt.colRange(cc, dd));
        }
        return matchange(im, ttt);
    }

    private Mat matchange(Mat A, Mat B) {
        ByteBuffer aBuffer = (ByteBuffer) A.asBuffer();
        byte[] aBytes = new byte[aBuffer.capacity()];
        aBuffer.get(aBytes);
        return new Mat(B.rows(), B.cols(), opencv_core.CV_64F, new BytePointer(ByteBuffer.wrap(aBytes)));
    }

    private Result doDetect(Mat image) {
        boolean hasError = false;
        float res = -1f;
        opencv_imgproc.resize(image, image, new Size(), 0.3, 0.3, 1);
        opencv_imgproc.cvtColor(image, image, opencv_imgproc.COLOR_BGR2GRAY);
        image.convertTo(image, opencv_core.CV_64F);
        Mat imgh0 = new Mat();
        Mat imgv0 = new Mat();
        Mat imgh1 = new Mat();
        Mat imgv1 = new Mat();
        Mat kh = new Mat(1, 3, opencv_core.CV_64F);
        DoubleRawIndexer khIndexer = kh.createIndexer();
        khIndexer.put(0, 0, -0.5);
        khIndexer.put(0, 1, 0);
        khIndexer.put(0, 2, 0.5);
        opencv_imgproc.filter2D(image, imgh0, image.depth(), kh);
        imgh0.colRange(1, imgh0.cols() - 1).copyTo(imgh1);
        Mat imgh = imgh1.mul(imgh1).asMat();
        Mat kv = new Mat(3, 1, opencv_core.CV_64F);
        DoubleRawIndexer kvIndexer = kv.createIndexer();
        kvIndexer.put(0, 0, -0.5);
        kvIndexer.put(1, 0, 0);
        kvIndexer.put(2, 0, 0.5);
        opencv_imgproc.filter2D(image, imgv0, image.depth(), kv);
        imgv0.rowRange(1, imgv0.rows() - 1).copyTo(imgv1);
        Mat imgv = imgv1.mul(imgv1).asMat();
        Mat Dh = convmxtH(kh);
        Mat Dh_t = Dh.t().asMat();
        Mat DHT = new Mat();
        opencv_core.gemm(Dh_t, Dh, 1, new Mat(), 0, DHT);
        Mat Dv = convmxtV(kv);
        Mat Dv_t = Dv.t().asMat();
        Mat DVT = new Mat();
        opencv_core.gemm(Dv_t, Dv, 1, new Mat(), 0, DVT);
        Mat DD = new Mat();
        opencv_core.add(DHT, DVT, DD);
        double tau0 = 81.8208;
        Mat patchsize = new Mat(1, 2, opencv_core.CV_32S, new Scalar(7, 7));
        IntRawIndexer patchsizeIndexer = patchsize.createIndexer();
        patchsizeIndexer.put(0, 0, 7);
        patchsizeIndexer.put(0, 1, 7);
        Mat patchsize_h = new Mat(1, 2, opencv_core.CV_32S, new Scalar(7, 5));
        IntRawIndexer patchsize_hIndexer = patchsize_h.createIndexer();
        patchsize_hIndexer.put(0, 0, 7);
        patchsize_hIndexer.put(0, 1, 5);
        Mat patchsize_v = new Mat(1, 2, opencv_core.CV_32S, new Scalar(5, 7));
        IntRawIndexer patchsize_vIndexer = patchsize_v.createIndexer();
        patchsize_vIndexer.put(0, 0, 5);
        patchsize_vIndexer.put(0, 1, 7);
        Mat X, Xh, Xv;
        X = im2col(image, patchsize);
        Xh = im2col(imgh, patchsize_h);
        Xv = im2col(imgv, patchsize_v);
        Mat Xt = new Mat(Xh.rows() * 2, Xh.cols(), opencv_core.CV_64F);
        Xh.copyTo(Xt.rowRange(0, Xt.rows() / 2));
        Xv.copyTo(Xt.rowRange(Xt.rows() / 2, Xt.rows()));
        Mat Xtr = new Mat(1, Xt.cols(), opencv_core.CV_64F);
        opencv_core.reduce(Xt, Xtr, 0, opencv_core.REDUCE_SUM);
        Mat temp = new Mat();
        opencv_core.gemm(X, X.t().asMat(), 1, new Mat(), 0, temp);
        Mat cov = opencv_core.divide(temp, X.cols() - 1).asMat();
        Mat eValuesMat = new Mat();
        Mat eVectorsMat = new Mat();
        opencv_core.eigen(cov, eValuesMat, eVectorsMat);
        DoubleRawIndexer eValuesMatIndexer = eValuesMat.createIndexer();
        double sig2 = eValuesMatIndexer.get(eValuesMat.rows() - 1, 0);
        double tau;
        Mat cov2 = new Mat();
        Mat eVal2 = new Mat();
        Mat eVec2 = new Mat();
        Mat Xtr2 = Xtr.clone();
        DoubleRawIndexer Xtr2Indexer = Xtr2.createIndexer();
        Mat X2 = X.clone();
        for (int k = 0; k < 1; k++) {
            tau = sig2 * tau0;
            Mat Xtrout = new Mat();
            Mat Xout = new Mat(X2.size(), opencv_core.CV_64F);
            double gg;
            Mat hh;
            int w = 0;
            for (int i = 0; i < Xtr2.cols(); i++) {
                gg = Xtr2Indexer.get(0, i);
                hh = X2.col(i);
                if (gg < tau) {
                    w = w + 1;
                    Xtrout.push_back(new Mat(gg));
                    hh.copyTo(Xout.colRange(w - 1, w));
                }
            }
            Xtrout = Xtrout.t().asMat();
            Xout = Xout.colRange(0, w);
            Mat temp2 = new Mat();
            opencv_core.multiply(Xout, Xout.t().asMat(), temp2);
            cov2 = opencv_core.divide(temp2, Xout.cols() - 1).asMat();
            opencv_core.eigen(cov2, eVal2, eVec2);
            DoubleRawIndexer eVal2Indexer = eVal2.createIndexer();
            sig2 = eVal2Indexer.get(eVal2.rows() - 1, 0);
            if (sig2 > threshold) {
                res = (float) sig2;
                hasError = true;
                break;
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
        Mat image = opencv_imgcodecs.imread(imagePath);
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
