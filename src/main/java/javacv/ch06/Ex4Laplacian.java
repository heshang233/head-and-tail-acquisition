package javacv.ch06;


import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static javacv.Helper.toMat8U;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;


/**
 * Created by liulongbiao on 16-6-16.
 */
public class Ex4Laplacian {
    public static void main(String[] args) throws IOException {
        Mat src = load(new File("data/boldt.jpg"), IMREAD_GRAYSCALE);

        // Compute floating point Laplacian edge strength
        LaplacianZC laplacian = new LaplacianZC();
        laplacian.aperture = 7;
        Mat flap = laplacian.computeLaplacian(src);
        show(toMat8U(flap), "Laplacian");

        // Locate edges using zero-crossing
        Mat edges = laplacian.getZeroCrossings(flap);
        show(edges, "Laplacian Edges");
    }
}
