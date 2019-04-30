package javacv.ch02;


import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_core.addWeighted;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;

/**
 * Created by liulongbiao on 16-6-9.
 */
public class Ex4BlendImages {

    public static void main(String[] args) throws IOException {
        Mat image1 = Helper.load(new File("data/boldt.jpg"), IMREAD_COLOR);
        Mat image2 = Helper.load(new File("data/rain.jpg"), IMREAD_COLOR);

        // Define output image
        Mat result = new Mat();

        // Create blended image
        addWeighted(image1, 0.7, image2, 0.9, 0.0, result);

        Helper.show(result, "Blended");
    }
}
