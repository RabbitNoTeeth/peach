package fun.bookish.peach;

import fun.bookish.peach.detector.*;
import fun.bookish.peach.utils.ImageUtils;

import java.util.List;

public class App {

    public static void main(String[] args) {
//        brightnessDetect();
//        sharpnessDetect();
//        stripeDetect();
        noiseDetect();
//        colorCastDetect();
    }

    /**
     * 色偏检测
     */
    private static void colorCastDetect() {
        List<String> imageList = ImageUtils.getImageList("E:\\projects\\peach\\images\\colorcast");
        imageList.forEach(image -> {
            ColorCastDetector detector = new ColorCastDetector();
            ColorCastDetector.Result res = detector.detect(image);
            String sb = "色偏检测(" + res.getThreshold() + ") - " +
                    "文件: " + image + ", " +
                    "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                    "(" + res.getResult() + ")";
            System.out.println(sb);
            ImageUtils.show(image, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 噪声检测
     */
    private static void noiseDetect() {
        List<String> imageList = ImageUtils.getImageList("E:\\projects\\peach\\images\\noise");
        imageList.forEach(image -> {
            NoiseDetector detector = new NoiseDetector();
            NoiseDetector.Result res = detector.detect(image);
            String sb = "噪声检测(" + res.getThreshold() + ") - " +
                    "文件: " + image + ", " +
                    "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                    "(" + res.getResult() + ")";
            System.out.println(sb);
            ImageUtils.show(image, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 条纹检测
     */
    private static void stripeDetect() {
        List<String> imageList = ImageUtils.getImageList("E:\\projects\\peach\\images\\stripe");
        imageList.forEach(image -> {
            StripeDetector detector = new StripeDetector();
            StripeDetector.Result res = detector.detect(image);
            String sb = "条纹检测(" + res.getThreshold() + ") - " +
                    "文件: " + image + ", " +
                    "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                    "(" + res.getResult() + ")";
            System.out.println(sb);
            ImageUtils.show(image, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 清晰度检测
     */
    private static void sharpnessDetect() {
        List<String> imageList = ImageUtils.getImageList("E:\\projects\\peach\\images\\sharpness");
        imageList.forEach(image -> {
            SharpnessDetector detector = new SharpnessDetector();
            SharpnessDetector.Result res = detector.detect(image);
            String sb = "清晰度检测(" + res.getThreshold() + ") - " +
                    "文件: " + image + ", " +
                    "是否正常: " + (res.isHasError() ? "[x]" : "[√]") +
                    "(" + res.getResult() + ")";
            System.out.println(sb);
            ImageUtils.show(image, res.isHasError() ? "[x]" : "[y]");
        });
    }

    /**
     * 亮度检测
     */
    private static void brightnessDetect() {
        List<String> imageList = ImageUtils.getImageList("E:\\projects\\peach\\images\\bright");
        imageList.forEach(image -> {
            BrightnessDetector brightnessDetector = new BrightnessDetector();
            BrightnessDetector.Result res = brightnessDetector.detect(image);
            String sb = "亮度检测(" + res.getThreshold() + ") - " +
                    "文件: " + image + ", " +
                    "是否正常: " + (res.isHasError() ? (res.getError() == BrightnessDetector.ErrorType.OVER_BRIGHT ? "[x] 过亮" : "[x] 过暗") : "[√]") + " " +
                    "(" + res.getResult() + ")";
            System.out.println(sb);
            ImageUtils.show(image, res.isHasError() ? "[x]" : "[y]");
        });
    }

}
