package cn.huangsy.video;

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
import static javacv.Helper.save;
import static javacv.Helper.show;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;

/**
 * @author huangsy
 * @date 2019/5/8 16:14
 */
public class CutVideo {

    public static void main(String[] args) throws IOException {

        String host = "D:/BaiduNetdiskDownload/S02E04.mp4";
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

//        CanvasFrame canvasFrame = new CanvasFrame("Extracted Frame", 1);
//        canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
//        // Exit the example when the canvas frame is closed
//        canvasFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
        Frame frame;
        int i = 0;

        while ((frame = grabber.grab()) != null ) {
            // Capture and show the frame
            if ( frame.image != null ){
                Mat convert = openCVConverter.convert(frame);
                save(new File("D:/BaiduNetdiskDownload/test2/"+i+".jpg"), convert);
                i++;
            }

            // Delay
//            Thread.sleep(delay);
        }

        // Close the video file
        grabber.release();
    }
}
