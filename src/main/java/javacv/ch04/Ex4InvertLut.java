package javacv.ch04;

import javacv.Helper;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;


/**
 * Created by liulongbiao on 16-6-15.
 */
public class Ex4InvertLut {
    public static void main(String[] args) throws IOException {
        Mat src = Helper.load(new File("data/group.jpg"), IMREAD_GRAYSCALE);

        // Create inverted lookup table
        int dim = 256;
        Mat lut = new Mat(1, dim, CV_8U);
        UByteIndexer lutI = lut.createIndexer();
        for (int i = 0; i < dim; i++) {
            lutI.put(i, (byte) (dim - 1 - i));
        }

        Mat dest = Histogram1D.applyLookUp(src, lut);

        Helper.show(dest, "Inverted LUT");
    }
}
