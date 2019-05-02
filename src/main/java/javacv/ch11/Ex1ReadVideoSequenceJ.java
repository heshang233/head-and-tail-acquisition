package javacv.ch11;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;

import static javacv.Helper.show;

/**
 * @author huangsy
 * @date 2019/4/30 16:55
 */
public class Ex1ReadVideoSequenceJ {

    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {

        String host = "F:\\迅雷下载\\神秘博士.Doctor.Who.2005.S10E08.中英字幕.BD-HR.AAC.720p.x264-人人影视.mp4";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(host);
        // Open video video file
        grabber.start();

        // Prepare window to display frames
        CanvasFrame canvasFrame = new CanvasFrame("Extracted Frame", 1);
        canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
        // Exit the example when the canvas frame is closed
        canvasFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        long delay = Math.round(200d / grabber.getFrameRate());

        // Read frame by frame, stop early if the display window is closed
        OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat();
        Frame frame;
        while ((frame = grabber.grab()) != null && canvasFrame.isVisible()) {
            // Capture and show the frame
            if (frame.timestamp == 0){
                System.out.println(frame.keyFrame);
                Mat convert = openCVConverter.convert(frame);
                show(convert, "zero");
                System.out.println(frame.timestamp);
//                canvasFrame.showImage(frame);
            }
            // Delay
            Thread.sleep(delay);
        }

        // Close the video file
        grabber.release();

    }
}
