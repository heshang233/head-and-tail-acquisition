package javacv.ch04;


import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;

import static java.lang.Math.round;
import static javacv.utils.Floats.sum;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;

/**
 * Created by liulongbiao on 16-6-15.
 */
public class Ex1ComputeHistogram {

    public static void main(String[] args) throws IOException {
        Mat src = Helper.load(new File("data/group.jpg"), IMREAD_GRAYSCALE);

        // Calculate histogram
        Histogram1D h = new Histogram1D();
        float[] histogram = h.getHistogramAsArray(src);

        // Print histogram values, the Scala way
        int bin = 0;
        for(float count: histogram) {
            System.out.println("" + bin + ": " + round(count));
            bin++;
        }

        // Validate histogram computations
        int numberOfPixels = src.cols() * src.rows();
        System.out.println("Number of pixels     : " + numberOfPixels);
        float sumOfHistogramBins = round(sum(histogram));
        System.out.println("Sum of histogram bins: " + sumOfHistogramBins);
        assert numberOfPixels == sumOfHistogramBins; // "Number of pixels must be equal the sum of histogram bins");
    }
}
