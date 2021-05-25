package fun.bookish.peach.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageUtils {

    public static void show(String imagePath, String text) {
        Mat image = Imgcodecs.imread(imagePath);
        Imgproc.putText(image, text, new Point(5, 50), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 255), 2);
        HighGui.imshow(imagePath, image);
        HighGui.waitKey(0);
        HighGui.destroyWindow(imagePath);
    }

    public static List<String> getImageList(String path) {
        List<String> res = new ArrayList<>();
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null)
                Arrays.stream(subFiles).forEach(subFile -> {
                    String name = subFile.getName();
                    if (name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg")) {
                        res.add(subFile.getAbsolutePath());
                    }
                });
        }
        return res;
    }

}
