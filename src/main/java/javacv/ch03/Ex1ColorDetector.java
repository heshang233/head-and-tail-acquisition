package javacv.ch03;


import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;


/**
 * Created by liulongbiao on 16-6-12.
 */
public class Ex1ColorDetector {
    public static void main(String[] args) throws IOException {
        ColorDetector detector = new ColorDetector();
        Mat image = Helper.load(new File("data/boldt.jpg"), IMREAD_COLOR);
        detector.setColorDistanceThreshold(100);
        detector.setTargetColor(new ColorRGB(130, 190, 230));

        Mat dist = detector.process(image);
        Helper.show(dist, "result");
    }
}
