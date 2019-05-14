package cn.huangsy.video;

import cn.huangsy.algorithm.MeanHash;
import cn.huangsy.algorithm.PSNR;
import cn.huangsy.algorithm.SSIM;
import cn.huangsy.algorithm.TemplateMatch;
import javacv.ch04.ImageComparator;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;

/**
 * @author huangsy
 * @date 2019/4/30 8:55
 */
public class Test {

    public static final String M4S = "http://10.0.224.243/live_record/liangc_test_2019_04_17_1_400_444x444_422/playlist.m3u8";
    public static final String TS = "http://10.0.224.19/vod/wwy___3_wmv_4000309_200000_320x240_1734/vod.m3u8";
    public static final String VIDEO = "D:\\BaiduNetdiskDownload\\STKF_ChangYouZhongGuo_018_BRC-150824.ts";

    //视频帧图片存储路径
    public static String videoFramesPath = "F:/home";

    public static void main(String[] args) throws IOException, InterruptedException {
        File referenceImageFile = new File("data/test1.jpg");
        // Load reference image
        Mat reference = load(referenceImageFile, IMREAD_COLOR);
        // Setup comparator
        ImageComparator comparator = new ImageComparator(reference);
        show(reference, "reference");

        String host = VIDEO;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(host);
        // Open video video file
        grabber.start();
        int fps = grabber.getLengthInFrames();
//            System.out.println(fFmpegFrameGrabber.grabKeyFrame());
        System.out.println("时长 " + fps / grabber.getFrameRate());
        //根据帧数跳转
//        grabber.setFrameNumber(34);
        //根据时间跳转
//        grabber.setAudioTimestamp(2400000);

        // Prepare window to display frames
        CanvasFrame canvasFrame = new CanvasFrame("Extracted Frame", 1);
        canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
        // Exit the example when the canvas frame is closed
        canvasFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        double frameRate = grabber.getFrameRate();
//        System.out.println(frameRate);
//        long delay = Math.round(1000d / frameRate);

        // Read frame by frame, stop early if the display window is closed
        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
//        Frame frame;
//        while ((frame = grabber.grab()) != null ) {
//            // Capture and show the frame
//            canvasFrame.showImage(frame);
//            if ( frame.image != null ){
//
//                Mat convert = openCVConverter.convert(frame);
//                int imageSize = convert.cols() * convert.rows();
//                // Compute histogram match and normalize by image size.
//                // 1 means perfect match.
//                int iDiffNum = MeanHash.hashCompare(convert, reference);
//                if (iDiffNum <= 1){
//                    String desc = String.format("compare , iDiffNum: %s", iDiffNum);
//                    show(convert, desc);
//                }
////                double score = comparator.compare(convert) / imageSize;
////                System.out.println("score:"+score+", time:"+frame.timestamp/1000000);
////                if (score>0.99){
////                    String desc = String.format("compare , score: %6.4f", score);
////                    show(convert, desc);
////                }
////                if (frame.keyFrame){
////
////                    System.out.println("key frame:"+frame.keyFrame);
////                    System.out.println("time:"+grabber.getTimestamp());
////                    show(convert, "key frame");
////                }
//            }
//
//
//            // Delay
////            Thread.sleep(delay);
//        }

        Frame frame;
        Mat res = null;
        String ss = null;
        double s = 0d;
//        while ((frame = grabber.grab()) != null ) {
        while ((frame = grabber.grab()) != null &&frame.timestamp/1000000<60) {
            // Capture and show the frame
//            canvasFrame.showImage(frame);
//            System.out.println("time:"+grabber.getTimestamp());
            if ( frame.image != null ){
                Mat convert = openCVConverter.convert(frame);
                int imageSize = convert.cols() * convert.rows();
                // Compute histogram match and normalize by image size.
                // 1 means perfect match.
                double score = TemplateMatch.matchTemplates(convert,reference, TemplateMatch.TemplatesMatchMethod.CV_TM_CCOEFF_NORMED);
                System.out.println("score:"+score+", time:"+frame.timestamp/1000000);
                if(score>s){
                    String desc = String.format("compare , score: %6.4f , time %s", score,frame.timestamp/1000000);
                    s = score;
                    res = convert.clone();
                    ss = desc;
//                    show(convert, desc);
//                    break;
                }

            }


            // Delay
//            Thread.sleep(delay);
        }
        show(res, ss);

        // Close the video file
        grabber.release();
    }

}
