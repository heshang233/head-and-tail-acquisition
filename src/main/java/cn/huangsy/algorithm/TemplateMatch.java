package cn.huangsy.algorithm;

import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.bytedeco.opencv.global.opencv_core.minMaxLoc;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgproc.matchTemplate;

/**
 * @author huangsy
 * @date 2019/5/14 8:39
 */
public class TemplateMatch {

    public static void main(String[] args) throws IOException {
        Mat s1 = Helper.load(new File("data/00272.jpg"), IMREAD_COLOR);
        Mat s2 = Helper.load(new File("data/00271.jpg"), IMREAD_COLOR);
        System.out.println(matchTemplates(s1, s2, TemplatesMatchMethod.CV_TM_SQDIFF_NORMED));
        System.out.println(matchTemplates(s1, s2, TemplatesMatchMethod.CV_TM_CCORR_NORMED));
        System.out.println(matchTemplates(s1, s2, TemplatesMatchMethod.CV_TM_CCOEFF_NORMED));
    }

    public static double matchTemplates(Mat source, Mat template, TemplatesMatchMethod method) {
        boolean matchRes;
        Mat result = new Mat();

        matchTemplate(source, template, result, method.getMethod());

        Point minPt  = new Point();
        Point maxPt  = new Point();
        double[] minVal = new double[2];
        double[] maxVal = new double[2];

        minMaxLoc(result, minVal, maxVal, minPt, maxPt, null);
        return maxVal[0];
    }

    public enum TemplatesMatchMethod{
        //        平方差匹配法CV_TM_SQDIFF  利用平方差来进行匹配,最好匹配为0.     匹配越差,匹配值越大.
        CV_TM_SQDIFF(0),
        //        归一化平方差匹配法CV_TM_SQDIFF_NORMED
        CV_TM_SQDIFF_NORMED(1),
        //        相关匹配法CV_TM_CCORR 采用模板和图像间的乘法操作,所以较大的数表示匹配程度较高,  0表示最坏的匹配效果,    1表示最好的匹配效果
        CV_TM_CCORR(2),
        //        归一化相关匹配法CV_TM_CCORR_NORMED
        CV_TM_CCORR_NORMED(3),
        //        相关系数匹配法CV_TM_CCOEFF将模版对其均值的相对值与图像对其均值的相关值进行匹配,    1表示完美匹配,-1表示糟糕的匹配,0表示没有任何相关性(随机序列).
        CV_TM_CCOEFF(4),
        //        归一化相关系数匹配法CV_TM_CCOEFF_NORMED
        CV_TM_CCOEFF_NORMED(5);

        private int method;

        TemplatesMatchMethod(int method){
            this.method = method;
        }

        public int getMethod() {
            return method;
        }

        public void setMethod(int method) {
            this.method = method;
        }
    }
}
