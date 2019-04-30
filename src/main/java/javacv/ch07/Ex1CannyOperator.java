package javacv.ch07;


import java.io.File;
import java.io.IOException;

import org.bytedeco.opencv.opencv_core.Mat;
import static javacv.Helper.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;


/**
 * 使用 Canny 操作符检测轮廓线
 *
 * Created by liulongbiao on 16-6-17.
 */
public class Ex1CannyOperator {
    public static void main(String[] args) throws IOException {
        Mat src = load(new File("data/road.jpg"), IMREAD_GRAYSCALE);

        // Canny contours
        Mat contours = new Mat();
        int threshold1 = 125;
        int threshold2 = 350;
        int apertureSize = 3;
        Canny(src, contours, threshold1, threshold2, apertureSize, true /*L2 gradient*/);

        show(contours, "Canny Contours");
    }
}
