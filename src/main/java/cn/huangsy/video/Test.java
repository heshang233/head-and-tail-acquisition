package cn.huangsy.video;

import javacv.ch04.ImageComparator;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avcodec.AVPicture;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.swscale.SwsContext;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.highgui.HighGui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static javacv.Helper.load;
import static javacv.Helper.show;
import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avcodec.av_free_packet;
import static org.bytedeco.ffmpeg.global.avcodec.avcodec_close;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.avutil.av_free;
import static org.bytedeco.ffmpeg.global.swscale.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;

/**
 * @author huangsy
 * @date 2019/4/30 8:55
 */
public class Test {

    public static final String M4S = "http://10.0.224.243/live_record/liangc_test_2019_04_17_1_400_444x444_422/playlist.m3u8";
    public static final String TS = "http://10.0.224.19/vod/wwy___3_wmv_4000309_200000_320x240_1734/vod.m3u8";

    //视频帧图片存储路径
    public static String videoFramesPath = "F:/home";

    public static void main(String[] args) throws IOException, InterruptedException {
        File referenceImageFile = new File("data/s7.png");
        // Load reference image
        Mat reference = load(referenceImageFile, IMREAD_COLOR);
        // Setup comparator
        ImageComparator comparator = new ImageComparator(reference);
        show(reference, "reference");

        String host = "F:\\迅雷下载\\神秘博士.Doctor.Who.2005.S10E08.中英字幕.BD-HR.AAC.720p.x264-人人影视.mp4";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(host);
        // Open video video file
        grabber.start();

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
        Frame frame;
        while ((frame = grabber.grab()) != null ) {
            // Capture and show the frame
            canvasFrame.showImage(frame);
            if ( frame.image != null ){
                Mat convert = openCVConverter.convert(frame);
                int imageSize = convert.cols() * convert.rows();
                // Compute histogram match and normalize by image size.
                // 1 means perfect match.
                double score = comparator.compare(convert) / imageSize;
                System.out.println("score:"+score+", time:"+frame.timestamp);
                if (score>0.94){
                    String desc = String.format("compare , score: %6.4f", score);
                    show(convert, desc);
                }
            }


            // Delay
//            Thread.sleep(delay);
        }

        // Close the video file
        grabber.release();
    }

}
