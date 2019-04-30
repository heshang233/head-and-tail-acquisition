package javacv.ch04;


import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;


/**
 * Created by liulongbiao on 16-6-15.
 */
public class Ex5EqualizeHistogram {
    public static void main(String[] args) throws IOException {
        Mat src = Helper.load(new File("data/group.jpg"), IMREAD_GRAYSCALE);
        Helper.show(new Histogram1D().getHistogramImage(src), "Input histogram");

        // Apply look-up
        Mat dest = Histogram1D.equalize(src);
        Helper.show(dest, "Equalized Histogram");

        // Show histogram of the modified image
        Helper.show(new Histogram1D().getHistogramImage(dest), "Equalized histogram");
    }
}
