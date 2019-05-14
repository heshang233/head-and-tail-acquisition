package cn.huangsy.algorithm;

import javacv.Helper;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.mean;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * 实现步骤
 *  1.缩小尺寸：将图像缩小到8*8的尺寸，总共64个像素。
 *
 *  2.这一步的作用是去除图像的细节，只保留结构/明暗等基本信息，摒弃不同尺寸/比例带来的图像差异；这一步的作用是去除图像的细节，只保留结构/明暗等基本信息，摒弃不同尺寸/比例带来的图像差异；
 *
 *  3.简化色彩：将缩小后的图像，转为64级灰度，即所有像素点总共只有64种颜色；
 *
 *  4.计算平均值：计算所有64个像素的灰度平均值；
 *
 *  5.比较像素的灰度：将每个像素的灰度，与平均值进行比较，大于或等于平均值记为1，小于平均值记为0；
 *
 *  6.计算哈希值：将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图像的指纹。组合的次序并不重要，只要保证所有图像都采用同样次序就行了；
 *
 *  7.得到指纹以后，就可以对比不同的图像，看看64位中有多少位是不一样的。在理论上，这等同于”汉明距离”(Hamming distance,在信息论中，两个等长字符串之间的汉明距离是两个字符串对应位置的不同字符的个数)。
 *
 * 如果不相同的数据位数不超过5，就说明两张图像很相似；
 * 如果大于10，就说明这是两张不同的图像。
 * @author huangsy
 * @date 2019/5/10 13:30
 */
public class MeanHash {

    public static void main(String[] args) throws IOException {
        Mat s1 = Helper.load(new File("data/canal1.jpg"), IMREAD_COLOR);
        Mat s2 = Helper.load(new File("data/canal2.jpg"), IMREAD_COLOR);


        int iDiffNum = hashCompare(s1, s2, 1);

        if (iDiffNum <= 5){
            System.out.println("two images are very similar! ");
        } else if (iDiffNum > 10){
            System.out.println("they are two different images! ");
        } else {
            System.out.println("two image are somewhat similar!");
        }

    }

    public static int hashCompare(Mat s1, Mat s2, int product) {
        Mat matDst1 = new Mat();
        Mat matDst2 = new Mat();

        resize(s1, matDst1, new Size(8 * product, 8 * product), 0, 0, INTER_CUBIC);
        resize(s2, matDst2, new Size(8 * product, 8 * product), 0, 0, INTER_CUBIC);

        Mat temp1 = matDst1;
        Mat temp2 = matDst2;

        cvtColor(temp1 , matDst1, CV_BGR2GRAY);
        cvtColor(temp2 , matDst2, CV_BGR2GRAY);

        int iAvg1 = 0, iAvg2 = 0;
        int arr1[] = new int[8 * product * 8 * product], arr2[] = new int[8 * product * 8 * product];

        for (int i = 0; i < 8 * product; i++){
            BytePointer ptr1 = matDst1.ptr(i);
            BytePointer ptr2 = matDst2.ptr(i);
            int tmp = i * 8 * product;

            for (int j = 0; j < 8 * product; j++)
            {
                int tmp1 = tmp + j;

                arr1[tmp1] = ptr1.get(j) / (4* product) * (4* product);
                arr2[tmp1] = ptr2.get(j) / (4* product) * (4* product);

                iAvg1 += arr1[tmp1];
                iAvg2 += arr2[tmp1];
            }
        }

        iAvg1 /= 8 * product * 8 * product;
        iAvg2 /= 8 * product * 8 * product;

        for (int i = 0; i < 8 * product * 8 * product; i++)
        {
            arr1[i] = (arr1[i] >= iAvg1) ? 1 : 0;
            arr2[i] = (arr2[i] >= iAvg2) ? 1 : 0;
        }

        int iDiffNum = 0;

        for (int i = 0; i < 8 * product * 8 * product; i++){
            if (arr1[i] != arr2[i]){
                ++iDiffNum;
            }
        }

        return iDiffNum;
    }

}
