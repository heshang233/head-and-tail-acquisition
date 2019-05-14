package cn.huangsy.algorithm;

import javacv.Helper;
import org.bytedeco.opencv.global.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgcodecs.*;
import org.bytedeco.opencv.opencv_core.GpuMat;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatExpr;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.io.File;
import java.io.IOException;

import static java.lang.Math.log10;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;

/**
 * @author huangsy
 * @date 2019/5/13 10:42
 */
public class PSNR {

    public static void main(String[] args) throws IOException {
        Mat s1 = Helper.load(new File("data/00271.jpg"), IMREAD_COLOR);
        Mat s2 = Helper.load(new File("data/00272.jpg"), IMREAD_COLOR);

        double psnr = getPSNR(s1, s2); // 区间30-50之间，则为相似。低于30一下则匹配越差
        System.out.println(psnr);

        // 目前打出来的opencv包不支持GPU
//        double psnr_gpu_optimized = getPSNR_GPU_optimized(s1, s2);
//        System.out.println(psnr_gpu_optimized);
//
//        double psnr_gpu = getPSNR_GPU(s1, s2);
//        System.out.println(psnr_gpu);

    }



    public static double getPSNR(Mat I1, Mat I2){
        Mat mat = new Mat();
        absdiff(I1, I2, mat);
        mat.convertTo(mat, CV_32F);
        MatExpr mul = mat.mul(mat);


        Scalar s = sumElems(mul.asMat());

        double sse = s.get(0) + s.get(1) + s.get(2); // sum channels

        if( sse <= 1e-10) { // for small values return zero
            return 0;
        } else {
            double  mse =sse /(double)(I1.channels() * I1.total());
            System.out.println(mse);
            double psnr = 10.0*log10((255*255)/mse);
            return psnr;
        }
    }

//    public static double getPSNR1(Mat m0, Mat m1){
//        int D = 255;
//
//        return (10 * log10((D*D) / mse(m0, m1, true, false)));
//    }

//    public static double mse(Mat m0, Mat m1, boolean grayscale, boolean rooted){
//        double res = 0;
//        int H = m0.rows(), W = m0.cols(), blanks = 0;
//        for (int i = 0; i < H; i++){
//            for (int j = 0; j < W; j++) {
//                if (grayscale) {
//                    double p0 = m0.at<double>(i, j), p1 = m1.at<double>(i, j);
//                    if (mask) {
//                        if ((p0 > 254.0) || (p0 < 1.0)) {
//                            ++blanks;
//                            continue;
//                        }
//                    }
//                    double diff = abs(p0 - p1);
//                    res += diff * diff;
//                }
//            }
//        }

//    }

//    double mse(Mat & m0, Mat & m1, bool grayscale = false, bool rooted = false) {
//        double res = 0;
//        int H = m0.rows, W = m0.cols, blanks = 0;
//#pragma omp parallel for
//        for (int i = 0; i < H; i++)
//            for (int j = 0; j < W; j++) {
//                if (grayscale) {
//                    double p0 = m0.at<double>(i, j), p1 = m1.at<double>(i, j);
//                    if (mask) {
//                        if ((p0 > 254.0) || (p0 < 1.0)) {
//                            ++blanks;
//                            continue;
//                        }
//                    }
//                    double diff = abs(p0 - p1);
//                    res += diff * diff;
//                }
//                else {
//                    Vec3b p0 = m0.at<Vec3b>(i, j);
//                    Vec3b p1 = m1.at<Vec3b>(i, j);
//                    if (mask) {
//                        if ((p0.val[0] > 254 && p0.val[1] > 254 && p0.val[2] > 254) || (p1.val[0] < 1 && p1.val[1] < 1 && p1.val[2] < 1)) {
//                            ++blanks;
//                            continue;
//                        }
//                    }
//                    double d0 = abs(p0.val[0] - p1.val[0]);
//                    double d1 = abs(p0.val[1] - p1.val[1]);
//                    double d2 = abs(p0.val[2] - p1.val[2]);
//                    if (rooted) {
//                        res += sqrt(d0 * d0 + d1 * d1 + d2 * d2) / 255.0 / sqrt(3.0);
//                    }
//                    else {
//                        res += (d0 * d0 + d1 * d1 + d2 * d2) / 3.0;
//                    }
//                }
//            }
//        res /= H * W - blanks;
//        return res;
//    }


//    double getPSNR(const Mat& I1, const Mat& I2)
//    {
//        Mat s1;
//        absdiff(I1, I2, s1);       // |I1 - I2|
//        s1.convertTo(s1, CV_32F);  // cannot make a square on 8 bits
//        s1 = s1.mul(s1);           // |I1 - I2|^2
//
//        Scalar s = sum(s1);         // sum elements per channel
//
//        double sse = s.val[0] + s.val[1] + s.val[2]; // sum channels
//
//        if( sse <= 1e-10) // for small values return zero
//            return 0;
//        else
//        {
//            double  mse =sse /(double)(I1.channels() * I1.total());
//            double psnr = 10.0*log10((255*255)/mse);
//            return psnr;
//        }
//    }


    public static double getPSNR_GPU_optimized(Mat I1, Mat I2){
        GpuMat gI1 = new GpuMat();
        gI1.upload(I1);

        GpuMat gI2 = new GpuMat();
        gI2.upload(I2);

        GpuMat t1 = new GpuMat();
        GpuMat t2 = new GpuMat();
        gI1.convertTo(t1, CV_32F);
        gI2.convertTo(t2, CV_32F);

        GpuMat gs = new GpuMat();
        absdiff(t1.reshape(1), t2.reshape(1), gs);
        multiply(gs, gs, gs);

        GpuMat buf = new GpuMat();
        double sse = sumElems(gs).get(0);

        if( sse <= 1e-10){// for small values return zero
            return 0;
        } else {
            double mse = sse /(double)(I1.channels() * I1.total());
            double psnr = 10.0*log10((255*255)/mse);
            return psnr;
        }
    }
//
//
//
//    double getPSNR_GPU_optimized(const Mat& I1, const Mat& I2, BufferPSNR& b)
//    {
//        b.gI1.upload(I1);
//        b.gI2.upload(I2);
//
//        b.gI1.convertTo(b.t1, CV_32F);
//        b.gI2.convertTo(b.t2, CV_32F);
//
//        gpu::absdiff(b.t1.reshape(1), b.t2.reshape(1), b.gs);
//        gpu::multiply(b.gs, b.gs, b.gs);
//
//        double sse = gpu::sum(b.gs, b.buf)[0];
//
//        if( sse <= 1e-10) // for small values return zero
//            return 0;
//        else
//        {
//            double mse = sse /(double)(I1.channels() * I1.total());
//            double psnr = 10.0*log10((255*255)/mse);
//            return psnr;
//        }
//    }
//


    public static double getPSNR_GPU(Mat I1, Mat I2){
        GpuMat gI1 = new GpuMat(), gI2  = new GpuMat(), gs  = new GpuMat(), t1  = new GpuMat(),t2 = new GpuMat();
        gI1.upload(I1);
        gI2.upload(I2);

        gI1.convertTo(t1, CV_32F);
        gI2.convertTo(t2, CV_32F);

        absdiff(t1.reshape(1), t2.reshape(1), gs);
        multiply(gs, gs, gs);

        Scalar s = sumElems(gs);
        double sse = s.get(0) + s.get(1) + s.get(2); // sum channels

        if( sse <= 1e-10) { // for small values return zero
            return 0;
        } else {
            double  mse =sse /(double)(gI1.channels() * I1.total());
            double psnr = 10.0*log10((255*255)/mse);
            return psnr;
        }
    }
//
//    double getPSNR_GPU(const Mat& I1, const Mat& I2)
//    {
//        gpu::GpuMat gI1, gI2, gs, t1,t2;
//
//        gI1.upload(I1);
//        gI2.upload(I2);
//
//        gI1.convertTo(t1, CV_32F);
//        gI2.convertTo(t2, CV_32F);
//
//        gpu::absdiff(t1.reshape(1), t2.reshape(1), gs);
//        gpu::multiply(gs, gs, gs);
//
//        Scalar s = gpu::sum(gs);
//        double sse = s.val[0] + s.val[1] + s.val[2];
//
//        if( sse <= 1e-10) // for small values return zero
//            return 0;
//        else
//        {
//            double  mse =sse /(double)(gI1.channels() * I1.total());
//            double psnr = 10.0*log10((255*255)/mse);
//            return psnr;
//        }
//    }
}
