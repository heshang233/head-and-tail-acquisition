package cn.huangsy.video;

import javacv.ch04.ColorHistogram;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * @author huangsy
 * @date 2019/5/3 3:14
 */
public class FrameComparator {

    //自定义阈值
    //相关性阈值，应大于多少，越接近1表示越像，最大为1
    public static final double HISTCMP_CORREL_THRESHOLD = 0.7;
    //卡方阈值，应小于多少，越接近0表示越像
    public static final double HISTCMP_CHISQR_THRESHOLD = 2;
    //交叉阈值，应大于多少，数值越大表示越像
    public static final double HISTCMP_INTERSECT_THRESHOLD = 1.2;
    //巴氏距离阈值，应小于多少，越接近0表示越像
    public static final double HISTCMP_BHATTACHARYYA_THRESHOLD = 0.3;

    private Mat referenceImage;
    private ColorHistogram hist;
    private Mat referenceHistogram;

    public FrameComparator(Mat referenceImage) {
        this(referenceImage, 8);
    }

    public FrameComparator(Mat referenceImage, int numberOfBins) {
        hist = new ColorHistogram();
        this.referenceImage = referenceImage;
        hist.setNumberOfBins(numberOfBins);
        referenceHistogram = hist.getHistogram(referenceImage);
    }

    public Mat getReferenceImage() {
        return referenceImage;
    }

    /**
     * Compare the reference image with the given input image and return similarity score.
     */
    public boolean compare(Mat image) {
        double result0, result1, result2, result3;
        Mat inputH = hist.getHistogram(image);
        result0 = compareHist(referenceHistogram, inputH, HISTCMP_CORREL);
        result1 = compareHist(referenceHistogram, inputH, HISTCMP_CHISQR);
        result2 = compareHist(referenceHistogram, inputH, HISTCMP_INTERSECT);
        result3 = compareHist(referenceHistogram, inputH, HISTCMP_BHATTACHARYYA);

        System.out.println("相关性（度量越高，匹配越准确 [基准："+HISTCMP_CORREL_THRESHOLD+"]）,当前值:" + result0);
        System.out.println("卡方（度量越低，匹配越准确 [基准："+HISTCMP_CHISQR_THRESHOLD+"]）,当前值:" + result1);
        System.out.println("交叉核（度量越高，匹配越准确 [基准："+HISTCMP_INTERSECT_THRESHOLD+"]）,当前值:" + result2);
        System.out.println("巴氏距离（度量越低，匹配越准确 [基准："+HISTCMP_BHATTACHARYYA_THRESHOLD+"]）,当前值:" + result3);

        //一共四种方式，有三个满足阈值就算匹配成功
        int count = 0;
        if (result0 > HISTCMP_CORREL_THRESHOLD)
            count++;
        if (result1 < HISTCMP_CHISQR_THRESHOLD)
            count++;
        if (result2 > HISTCMP_INTERSECT_THRESHOLD)
            count++;
        if (result3 < HISTCMP_BHATTACHARYYA_THRESHOLD)
            count++;
        if (count >= 3) {
            //这是相似的图像
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
