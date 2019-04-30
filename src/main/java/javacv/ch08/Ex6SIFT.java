package javacv.ch08;


import org.bytedeco.opencv.opencv_core.KeyPointVector;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_xfeatures2d.SIFT;

import java.io.File;
import java.io.IOException;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_features2d.DRAW_RICH_KEYPOINTS;
import static org.bytedeco.opencv.global.opencv_features2d.drawKeypoints;


/**
 * Detecting the scale-invariant SIFT features
 *
 * Created by liulongbiao on 16-6-19.
 */
public class Ex6SIFT {
    public static void main(String[] args) throws IOException {
        Mat image = load(new File("data/s1.png"));
        // Detect SIFT features.
        KeyPointVector keyPoints = new KeyPointVector();
        int nFeatures = 0;
        int nOctaveLayers = 3;
        double contrastThreshold = 0.03;
        int edgeThreshold = 10;
        double sigma = 1.6;
        SIFT sift = SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
        sift.detect(image, keyPoints);

        // Draw keyPoints
        Mat featureImage = new Mat();
        drawKeypoints(image, keyPoints, featureImage, new Scalar(255, 255, 255, 0), DRAW_RICH_KEYPOINTS);
        show(featureImage, "SIFT Features");
    }
}
