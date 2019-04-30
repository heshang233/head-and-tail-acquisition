package javacv.ch08;


import org.bytedeco.opencv.opencv_core.KeyPointVector;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_features2d.GFTTDetector;

import java.io.File;
import java.io.IOException;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_features2d.DEFAULT;
import static org.bytedeco.opencv.global.opencv_features2d.drawKeypoints;


/**
 * Example of using the Good Features to Track detector.
 *
 * Created by liulongbiao on 16-6-19.
 */
public class Ex3GoodFeaturesToTrack {

    public static void main(String[] args) throws IOException {
        Mat image = load(new File("data/church01.jpg"));
        // Compute good features to track
        GFTTDetector gftt      = GFTTDetector.create(
                500 /* maximum number of corners to be returned */ ,
                0.01 /* quality level*/ ,
                10.0 /* minimum allowed distance between points*/ ,
                3 /* block size*/ ,
                false /* use Harris detector*/ ,
                0.04 /* Harris parameter */
        );
        KeyPointVector keyPoints = new KeyPointVector();
        gftt.detect(image, keyPoints);

        // Draw keyPoints
        Mat canvas = new Mat();
        drawKeypoints(image, keyPoints, canvas, new Scalar(255, 255, 255, 0), DEFAULT);
        show(canvas, "Good Features to Track Detector");
    }
}
