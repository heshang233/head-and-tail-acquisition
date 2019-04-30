package javacv.ch08;


import org.bytedeco.opencv.opencv_core.KeyPointVector;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_features2d.FastFeatureDetector;

import java.io.File;
import java.io.IOException;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_features2d.DEFAULT;
import static org.bytedeco.opencv.global.opencv_features2d.drawKeypoints;


/**
 * Detecting FAST features
 *
 * Created by liulongbiao on 16-6-19.
 */
public class Ex4FAST {
    public static void main(String[] args) throws IOException {
        Mat image = load(new File("data/s1.png"));
        // Detect FAST features
        FastFeatureDetector ffd = FastFeatureDetector.create(
                40 /* threshold for detection */ ,
                true /* non-max suppression */ ,
                FastFeatureDetector.TYPE_9_16);
        KeyPointVector keyPoints = new KeyPointVector();
        ffd.detect(image, keyPoints);

        // Draw keyPoints
        Mat canvas = new Mat();
        drawKeypoints(image, keyPoints, canvas, new Scalar(255, 255, 255, 0), DEFAULT);
        show(canvas, "FAST Features");
    }
}
