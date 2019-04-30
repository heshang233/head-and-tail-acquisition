package javacv.ch01;


import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Created by liulongbiao on 16-6-7.
 */
public class LoadAndSave {

    public static void main(String[] args) throws IOException {
        Mat image = Helper.load(new File("data/puppy.bmp"), IMREAD_COLOR);
        Helper.show(image, "source image");

        Mat result = new Mat();
        flip(image, result, 1);
        Helper.show(result, "flip image");
        imwrite("dist/puppy_flip.bmp", result);

        Mat image3 = image.clone();
        circle(image3, // 目标图片
                new Point(155, 110), // 中心坐标
                65, // 半径
                new Scalar(0), // 颜色，这里为黑色
                3, // 厚度
                8, // 8 连接线
                0); // 偏移
        putText(image3, // 目标图片
                "This is a dog", // 文本
                new Point(40, 200), // 文本位置
                FONT_HERSHEY_PLAIN, // 字体类型
                2.0, // 字体缩放
                new Scalar(255), // 文本颜色，这里为白色
                2, // 文本厚度
                8, // 线条类型
                false); // 当该值为真时，原点位于左下角，否则位于左上角
        Helper.show(image3, "text image");
    }
}
