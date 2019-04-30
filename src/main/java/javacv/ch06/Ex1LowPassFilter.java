package javacv.ch06;


import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.File;
import java.io.IOException;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_core.BORDER_DEFAULT;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.blur;

/**
 * 低通滤波器。
 * 对高斯滤波器的基本使用
 *
 * Created by liulongbiao on 16-6-16.
 */
public class Ex1LowPassFilter {
    public static void main(String[] args) throws IOException {
        Mat src = load(new File("data/boldt.jpg"), IMREAD_GRAYSCALE);
        // Blur with a Gaussian filter
        //    val dest = cvCreateImage(cvGetSize(src), src.depth, 1)
        Mat dest = new Mat();
        Size kernelSize = new Size(5, 5);
        double sigma = 1.5;
        int borderType = BORDER_DEFAULT;
        blur(src, dest, kernelSize);
        show(dest, "Blurred");
    }
}
