package javacv.ch04;


import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;


/**
 * Created by liulongbiao on 16-6-15.
 */
public class Ex3Threshold {

    public static void main(String[] args) throws IOException {
        Mat src = Helper.load(new File("data/group.jpg"), IMREAD_GRAYSCALE);

        Mat dest = new Mat();
        threshold(src, dest, 60, 255, THRESH_BINARY);

        Helper.show(dest, "Thresholded");
    }

}
