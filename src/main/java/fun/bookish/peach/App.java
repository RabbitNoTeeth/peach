package fun.bookish.peach;

import fun.bookish.peach.detector.*;
import fun.bookish.peach.utils.ImageUtils;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.List;

public class App {

    public static void main(String[] args) {
//        brightnessBatchDetect("E:\\projects\\peach\\images\\bright");
//        sharpnessBatchDetect("E:\\projects\\peach\\images\\sharpness");
//        stripeBatchDetect("E:\\projects\\peach\\images\\stripe");
//        noiseBatchDetect("E:\\projects\\peach\\images\\noise");
//        colorCastNBatchDetect("E:\\projects\\peach\\images\\colorcast");
        batchTest("E:\\projects\\peach\\images\\test");
    }

    private static void batchTest(String dir) {
        List<String> imageList = ImageUtils.getImageList(dir);
        imageList.forEach(imagePath -> {
            Mat image = opencv_imgcodecs.imread(imagePath);
            // 偏色检测
            colorCastDetect(image, 2f);
            // 噪声检测
            noiseDetect(image, 22.0f);
            // 条纹检测
            stripeDetect(image, 0.0085f);
            // 清晰度检测
            sharpnessDetect(image, 10.0f);
            // 亮度检测
            brightnessDetect(image, 0f);
            ImageUtils.show(imagePath, "");
        });
    }

    /**
     * 偏色检测
     * @param image
     */
    private static ColorCastDetector.Result colorCastDetect(Mat image) {
        ColorCastDetector detector = new ColorCastDetector();
        ColorCastDetector.Result res = detector.detect(image);
        String sb = "色偏检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(da=" + res.getDa() + ", db=" + res.getDb() + ", result=" + res.getResult() + ", errorType=" + res.getErrorType() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 偏色检测
     * @param image
     */
    private static ColorCastDetector.Result colorCastDetect(Mat image, float threshold) {
        ColorCastDetector detector = new ColorCastDetector(threshold);
        ColorCastDetector.Result res = detector.detect(image);
        String sb = "色偏检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(da=" + res.getDa() + ", db=" + res.getDb() + ", result=" + res.getResult() + ", errorType=" + res.getErrorType() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 批量偏色检测
     */
    private static void colorCastBatchDetect(String dir) {
        List<String> imageList = ImageUtils.getImageList(dir);
        imageList.forEach(imagePath -> {
            Mat image = opencv_imgcodecs.imread(imagePath);
            ColorCastDetector.Result res = colorCastDetect(image);
            ImageUtils.show(imagePath, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 噪声检测
     */
    private static  NoiseDetector.Result noiseDetect(Mat image) {
        NoiseDetector detector = new NoiseDetector();
        NoiseDetector.Result res = detector.detect(image);
        String sb = "噪声检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 噪声检测
     */
    private static  NoiseDetector.Result noiseDetect(Mat image, float threshold) {
        NoiseDetector detector = new NoiseDetector(threshold);
        NoiseDetector.Result res = detector.detect(image);
        String sb = "噪声检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 噪声检测
     */
    private static void noiseBatchDetect(String dir) {
        List<String> imageList = ImageUtils.getImageList(dir);
        imageList.forEach(imagePath -> {
            Mat image = opencv_imgcodecs.imread(imagePath);
            NoiseDetector.Result res = noiseDetect(image);
            ImageUtils.show(imagePath, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 条纹检测
     */
    private static StripeDetector.Result stripeDetect(Mat image) {
        StripeDetector detector = new StripeDetector();
        StripeDetector.Result res = detector.detect(image);
        String sb = "条纹检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 条纹检测
     */
    private static StripeDetector.Result stripeDetect(Mat image, float threshold) {
        StripeDetector detector = new StripeDetector(threshold);
        StripeDetector.Result res = detector.detect(image);
        String sb = "条纹检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 条纹检测
     */
    private static void stripeBatchDetect(String dir) {
        List<String> imageList = ImageUtils.getImageList(dir);
        imageList.forEach(imagePath -> {
            Mat image = opencv_imgcodecs.imread(imagePath);
            StripeDetector.Result res = stripeDetect(image);
            ImageUtils.show(imagePath, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 清晰度检测
     */
    private static SharpnessDetector.Result sharpnessDetect(Mat image) {
        SharpnessDetector detector = new SharpnessDetector();
        SharpnessDetector.Result res = detector.detect(image);
        String sb = "清晰度检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 清晰度检测
     */
    private static SharpnessDetector.Result sharpnessDetect(Mat image, float threshold) {
        SharpnessDetector detector = new SharpnessDetector(threshold);
        SharpnessDetector.Result res = detector.detect(image);
        String sb = "清晰度检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 清晰度检测
     */
    private static void sharpnessBatchDetect(String dir) {
        List<String> imageList = ImageUtils.getImageList(dir);
        imageList.forEach(imagePath -> {
            Mat image = opencv_imgcodecs.imread(imagePath);
            SharpnessDetector.Result res = sharpnessDetect(image);
            ImageUtils.show(imagePath, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 亮度检测
     */
    private static BrightnessDetector.Result brightnessDetect(Mat image) {
        BrightnessDetector brightnessDetector = new BrightnessDetector();
        BrightnessDetector.Result res = brightnessDetector.detect(image);
        String sb = "亮度检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? (res.getError() == BrightnessDetector.ErrorType.OVER_BRIGHT ? "[x] 过亮" : "[x] 过暗") : "[√]") + " " +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 亮度检测
     */
    private static BrightnessDetector.Result brightnessDetect(Mat image, float threshold) {
        BrightnessDetector brightnessDetector = new BrightnessDetector(threshold);
        BrightnessDetector.Result res = brightnessDetector.detect(image);
        String sb = "亮度检测(" + res.getThreshold() + ") - " +
                "文件: " + image + ", " +
                "是否正常: " + (res.isHasError() ? (res.getError() == BrightnessDetector.ErrorType.OVER_BRIGHT ? "[x] 过亮" : "[x] 过暗") : "[√]") + " " +
                "(" + res.getResult() + ")";
        System.out.println(sb);
        return res;
    }

    /**
     * 亮度检测
     */
    private static void brightnessBatchDetect(String dir) {
        List<String> imageList = ImageUtils.getImageList(dir);
        imageList.forEach(imagePath -> {
            Mat image = opencv_imgcodecs.imread(imagePath);
            BrightnessDetector.Result res = brightnessDetect(image);
            ImageUtils.show(imagePath, res.isHasError() ? "[x]" : "[y]");
        });
    }

}
