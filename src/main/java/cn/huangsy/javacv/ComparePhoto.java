package cn.huangsy.javacv;

import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.Core;

import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

/**
 * @author huangsy
 * @date 2019/4/29 10:14
 */
public class ComparePhoto {

    public static void main(String[] args) {
        //读取原始图片
        Mat image = imread("D:\\home\\photo\\s1.png");
        if (image.empty()) {
            System.err.println("load photo error, plz check address!");
            return;
        }
        //显示图片
        imshow("show the photo", image);

        //无限等待按键按下
        waitKey(0);
    }
}
