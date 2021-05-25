package fun.bookish.peach;

import fun.bookish.peach.detector.BrightnessDetector;
import fun.bookish.peach.detector.SharpnessDetector;
import fun.bookish.peach.detector.StripeDetector;
import fun.bookish.peach.utils.ImageUtils;

import java.net.URL;
import java.util.List;

public class App {

    static {
        URL url = ClassLoader.getSystemResource("lib/opencv_java3414.dll");
        System.load(url.getPath());
    }

    public static void main(String[] args) {
//        brightnessDetect();
        sharpnessDetect();
//        stripeDetect();
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
            ImageUtils.show(image, "");
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
            ImageUtils.show(image, "");
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
            String sb = "亮度检测(" + res.getThreshold1() + "," + res.getThreshold2() + ") - " +
                    "文件: " + image + ", " +
                    "是否正常: " + (res.isHasError() ? (res.getErrorCode() == 1 ? "[x] 过亮" : "[x] 过暗") : "[√]") + " " +
                    "(" + res.getResult1() + ", " + res.getResult2() + ")";
            System.out.println(sb);
            ImageUtils.show(image, "");
        });
    }

}
