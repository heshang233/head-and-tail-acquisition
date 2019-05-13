package cn.huangsy.algorithm;

import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;

/**
 * @author huangsy
 * @date 2019/5/13 17:57
 */
public class SSIM {

    public static void main(String[] args) throws IOException {
        Mat s1 = Helper.load(new File("data/00271.jpg"), IMREAD_COLOR);
        Mat s2 = Helper.load(new File("data/00272.jpg"), IMREAD_COLOR);

        Scalar mssim = getMSSIM(s1, s2);
        System.out.println(mssim.get());
    }

    public static Scalar getMSSIM(Mat i1, Mat i2){
        Scalar C1 = new Scalar(6.5025), C2 = new Scalar(58.5225);
        /***************************** INITS **********************************/
        int d     = CV_32F;
        Mat I1 = new Mat(), I2 = new Mat();

        i1.convertTo(I1, d);
        i2.convertTo(I2, d);

        Mat I2_2   = I2.mul(I2).asMat();        // I2^2
        Mat I1_2   = I1.mul(I1).asMat();        // I1^2
        Mat I1_I2  = I1.mul(I2).asMat();        // I1 * I2
        /*************************** END INITS **********************************/

        Mat mu1 = new Mat(), mu2 = new Mat();   // PRELIMINARY COMPUTING
        GaussianBlur(I1, mu1, new Size(11, 11), 1.5);
        GaussianBlur(I2, mu2, new Size(11, 11), 1.5);

        Mat mu1_2   =   mu1.mul(mu1).asMat();
        Mat mu2_2   =   mu2.mul(mu2).asMat();
        Mat mu1_mu2 =   mu1.mul(mu2).asMat();

        Mat sigma1_2 = new Mat(), sigma2_2 = new Mat(), sigma12 = new Mat();

        GaussianBlur(I1_2, sigma1_2, new Size(11, 11), 1.5);
        subtract(sigma1_2, mu1_2, sigma1_2);

        GaussianBlur(I2_2, sigma2_2, new Size(11, 11), 1.5);
        subtract(sigma2_2, mu2_2, sigma2_2);

        GaussianBlur(I1_I2, sigma12, new Size(11, 11), 1.5);
        subtract(sigma12, mu1_mu2, sigma12);

        ///////////////////////////////// FORMULA ////////////////////////////////
        Mat t1 = new Mat(), t2 = new Mat(), t3 = new Mat();

        t1 = add(multiply(2, mu1_mu2), C1).asMat();;
        //t2 = 2 * sigma12 + C2;
        t2 = add(multiply(2, sigma12), C2).asMat();
        t3 = t1.mul(t2).asMat();              // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))

        //t1 = mu1_2 + mu2_2 + C1;
        t1 = add(add(mu1_2, mu2_2), C1).asMat();

        //t2 = sigma1_2 + sigma2_2 + C2;
        t2 = add(add(sigma1_2, sigma2_2), C2).asMat();
        t1 = t1.mul(t2).asMat();               // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))

        Mat ssim_map = new Mat();
        divide(t3, t1, ssim_map);      // ssim_map =  t3./t1;

        Scalar mssim = mean( ssim_map ); // mssim = average of ssim map
        return mssim;

    }

//    Scalar getMSSIM( const Mat& i1, const Mat& i2)
//    {
//    const double C1 = 6.5025, C2 = 58.5225;
//        /***************************** INITS **********************************/
//        int d     = CV_32F;
//
//        Mat I1, I2;
//        i1.convertTo(I1, d);           // cannot calculate on one byte large values
//        i2.convertTo(I2, d);
//
//        Mat I2_2   = I2.mul(I2);        // I2^2
//        Mat I1_2   = I1.mul(I1);        // I1^2
//        Mat I1_I2  = I1.mul(I2);        // I1 * I2
//
//        /*************************** END INITS **********************************/
//
//        Mat mu1, mu2;   // PRELIMINARY COMPUTING
//        GaussianBlur(I1, mu1, Size(11, 11), 1.5);
//        GaussianBlur(I2, mu2, Size(11, 11), 1.5);
//
//        Mat mu1_2   =   mu1.mul(mu1);
//        Mat mu2_2   =   mu2.mul(mu2);
//        Mat mu1_mu2 =   mu1.mul(mu2);
//
//        Mat sigma1_2, sigma2_2, sigma12;
//
//        GaussianBlur(I1_2, sigma1_2, Size(11, 11), 1.5);
//        sigma1_2 -= mu1_2;
//
//        GaussianBlur(I2_2, sigma2_2, Size(11, 11), 1.5);
//        sigma2_2 -= mu2_2;
//
//        GaussianBlur(I1_I2, sigma12, Size(11, 11), 1.5);
//        sigma12 -= mu1_mu2;
//
//        ///////////////////////////////// FORMULA ////////////////////////////////
//        Mat t1, t2, t3;
//
//        t1 = 2 * mu1_mu2 + C1;
//        t2 = 2 * sigma12 + C2;
//        t3 = t1.mul(t2);              // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))
//
//        t1 = mu1_2 + mu2_2 + C1;
//        t2 = sigma1_2 + sigma2_2 + C2;
//        t1 = t1.mul(t2);               // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))
//
//        Mat ssim_map;
//        divide(t3, t1, ssim_map);      // ssim_map =  t3./t1;
//
//        Scalar mssim = mean( ssim_map ); // mssim = average of ssim map
//        return mssim;
//    }
}
