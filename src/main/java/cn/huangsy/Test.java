package cn.huangsy;

import cn.huangsy.algorithm.MeanHash;
import cn.huangsy.algorithm.PSNR;
import cn.huangsy.algorithm.SSIM;
import cn.huangsy.algorithm.TemplateMatch;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;

/**
 * @author huangsy
 * @date 2019/4/30 8:55
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        String photo = "data/video/test1.jpg";
        String video = "data/video/STKF_ChangYouZhongGuo_017_BRC-150825.ts";


//        List<Integer> a = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            a.add(i);
//        }
//        a.parallelStream().forEach( s ->{
//            try {
//                testSSIM(photo, video);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        testTemplate(photo, video);
//        testPSNR(photo, video);

        testPHash(photo, video, 1);
        testPHash(photo, video, 2);
        testPHash(photo, video, 4);
        testPHash(photo, video, 8);
    }


    public static void testTemplate(String comparePhotoPath, String videoPath) throws IOException {
        System.out.println("template start");
        File referenceImageFile = new File(comparePhotoPath);
        Mat reference = load(referenceImageFile, IMREAD_COLOR);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        grabber.start();
        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
        Frame frame;
        double score = 0d;
        long timestamp = 0L;
        Instant start = Instant.now();
        while ((frame = grabber.grab()) != null &&frame.timestamp/1000000<180) {
            if ( frame.image != null ){
                Mat convert = openCVConverter.convert(frame);
                //Template
                double score2 = TemplateMatch.matchTemplates(convert,reference, TemplateMatch.TemplatesMatchMethod.CV_TM_CCOEFF_NORMED);
                if(score2>score){
                    score = score2;
                    timestamp = frame.timestamp;
                }

            }
        }
        Instant end = Instant.now();
        System.out.println(" Template  score : " + score + " timestamp : " + timestamp + " ---- cost time : " + (end.getEpochSecond() - start.getEpochSecond()) );
        grabber.release();
        System.out.println("template end");
    }

    public static void testPHash(String comparePhotoPath, String videoPath, int product) throws IOException {
        System.out.println("PHash start");
        File referenceImageFile = new File(comparePhotoPath);
        Mat reference = load(referenceImageFile, IMREAD_COLOR);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        grabber.start();
        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
        Frame frame;
        int score = 64 * product * product;
        long timestamp = 0L;
        Mat res = null;
        Instant start = Instant.now();
        while ((frame = grabber.grab()) != null &&frame.timestamp/1000000<180) {
            if ( frame.image != null ){
                Mat convert = openCVConverter.convert(frame);
                //Template
                int score2 = MeanHash.hashCompare(convert,reference, product);
                if(score2<score){
                    score = score2;
                    timestamp = frame.timestamp;
                    res = convert.clone();
                }

            }
        }
        show(res, String.format("compare , score: %s , time %s", score,timestamp/1000000));
        Instant end = Instant.now();
        System.out.println(" PHash product: "+ product +"  score : " + score + " timestamp : " + timestamp + " ---- cost time : " + (end.getEpochSecond() - start.getEpochSecond()) );
        grabber.release();
        System.out.println("PHash end");
    }

    public static void testSSIM(String comparePhotoPath, String videoPath) throws IOException {
        System.out.println("SSIM start");
        File referenceImageFile = new File(comparePhotoPath);
        Mat reference = load(referenceImageFile, IMREAD_COLOR);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        grabber.start();
        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
        Frame frame;
        double score = 0d;
        long timestamp = 0L;
        Instant start = Instant.now();
        while ((frame = grabber.grab()) != null &&frame.timestamp/1000000<180) {
            if ( frame.image != null ){
                Mat convert = openCVConverter.convert(frame);
                //SSIM
                double score3 = SSIM.getMSSIM(convert,reference).get();
                if(score3>score){
                    score = score3;
                    timestamp = frame.timestamp;
                }

            }
        }
        Instant end = Instant.now();
        System.out.println(" Template  score : " + score + " timestamp : " + timestamp + " ---- cost time : " + (end.getEpochSecond() - start.getEpochSecond()) );
        grabber.release();
        System.out.println("SSIM end");
    }

    public static void testPSNR(String comparePhotoPath, String videoPath) throws IOException {
        System.out.println("PSNR start");
        File referenceImageFile = new File(comparePhotoPath);
        Mat reference = load(referenceImageFile, IMREAD_COLOR);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        grabber.start();
        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
        Frame frame;
        double score = 0d;
        long timestamp = 0L;
        Instant start = Instant.now();
        while ((frame = grabber.grab()) != null &&frame.timestamp/1000000<180) {
            if ( frame.image != null ){
                Mat convert = openCVConverter.convert(frame);
                //PSNR
                double score1 = PSNR.getPSNR(convert,reference);
                if(score1>score){
                    score = score1;
                    timestamp = frame.timestamp;
                }

            }
        }
        Instant end = Instant.now();
        System.out.println(" Template  score : " + score + " timestamp : " + timestamp + " ---- cost time : " + (end.getEpochSecond() - start.getEpochSecond()) );
        grabber.release();
        System.out.println("PSNR end");
    }

}
