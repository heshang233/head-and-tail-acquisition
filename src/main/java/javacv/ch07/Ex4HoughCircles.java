package javacv.ch07;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

import java.io.File;
import java.io.IOException;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_core.BORDER_DEFAULT;
import static org.bytedeco.opencv.global.opencv_core.cvRound;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * 使用霍夫方法检测圆
 *
 * Created by liulongbiao on 16-6-17.
 */
public class Ex4HoughCircles {
    public static void main(String[] args) throws IOException {
        Mat src = load(new File("data/chariot.jpg"), IMREAD_GRAYSCALE);

        // Blur with a Gaussian filter
        Mat smooth = new Mat();
        Size kernelSize = new Size(5, 5);
        double sigma = 1.5;
        int borderType = BORDER_DEFAULT;
        GaussianBlur(src, smooth, kernelSize, sigma, sigma, borderType);
        show(smooth, "Blurred");


        // Compute Hough Circle transform
        // accumulator resolution (size of the image / 2)
        int dp      = 2;
        // minimum distance between two circles
        int minDist = 33;
        // Canny high threshold
        int highThreshold = 200;
        // minimum number of votes
        int votes     = 100;
        int minRadius = 40;
        int maxRadius = 90;
        Mat circles   = new Mat();
        // TODO 此处api调用有问题
//        HoughCircles(smooth, circles, HOUGH_GRADIENT, dp, minDist, highThreshold, votes, minRadius, maxRadius);

        // Draw lines on the canny contour image
        Mat colorDst = new Mat();
        cvtColor(src, colorDst, COLOR_GRAY2BGR);
        FloatRawIndexer indexer = circles.createIndexer();
        for (int i = 0; i < circles.cols(); i++) {
            Point center = new Point(cvRound(indexer.get(0, i, 0)), cvRound(indexer.get(0, i, 1)));
            int radius = cvRound(indexer.get(0, i, 2));
            System.out.println("Circle ((" + center.x() + ", " + center.y() + "), " + radius + ")");
            circle(colorDst, center, radius, new Scalar(0, 0, 255, 0), 1, LINE_AA, 0);
        }
        show(colorDst, "Hough Circles");
    }
}
