package cn.huangsy.javacv;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_videoio;
import org.bytedeco.opencv.opencv_videoio.CvCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author huangsy
 * @date 2019/4/29 10:18
 */
public class OpenCVVideo {

    public static void main(String[] args) throws FrameGrabber.Exception {
//        FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(new File("D:\\BaiduNetdiskDownload\\S02E04.mp4"));
//        ff.start();
//
//
//        ff.setFrameNumber(200);
//        //获取第200帧在当前视频的时间戳
//        //单位 微妙  1秒 = 1000000微秒
//        long timestamp = ff.getTimestamp();
//        System.out.println(timestamp);

//        ff.start();
//        //获取第1000000微秒在当前视频的帧数
//        //单位 微妙  1秒 = 1000000微秒
//        ff.setAudioTimestamp(13399333);
//        long framenumber = ff.getFrameNumber();
//        System.out.println(framenumber);
        grabberVideoFramer("S02E04.mp4");
    }

    //视频文件路径
    private static String videoPath = "D:\\BaiduNetdiskDownload";

    //视频帧图片存储路径
    public static String videoFramesPath = "D:/home/photo";

    /**
     * TODO 将视频文件帧处理并以“jpg”格式进行存储。
     * 依赖FrameToBufferedImage方法：将frame转换为bufferedImage对象
     *
     * @param videoFileName
     */
    public static void grabberVideoFramer(String videoFileName) {
        //Frame对象
        Frame frame = null;
        //标识
        int flag = 0;
        /*
            获取视频文件
         */
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoPath + "/" + videoFileName);

        try {
            fFmpegFrameGrabber.start();
            /*
            .getFrameRate()方法：获取视频文件信息,总帧数
             */
            int ftp = fFmpegFrameGrabber.getLengthInFrames();
//            System.out.println(fFmpegFrameGrabber.grabKeyFrame());
            System.out.println("时长 " + ftp / fFmpegFrameGrabber.getFrameRate() / 60);

            BufferedImage bImage = null;
            System.out.println("开始运行视频提取帧，耗时较长");

            while (flag <= ftp) {
                //文件绝对路径+名字
                String fileName = videoFramesPath + "/img_" + String.valueOf(flag) + ".jpg";
                //文件储存对象
                File outPut = new File(fileName);
                //获取帧
                frame = fFmpegFrameGrabber.grabImage();
//                System.out.println(frame);
                if (frame != null) {
                    ImageIO.write(FrameToBufferedImage(frame), "jpg", outPut);
                }
                flag++;
            }
            System.out.println("============运行结束============");
            fFmpegFrameGrabber.stop();
        } catch (IOException E) {
        }
    }
    public static BufferedImage FrameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

}
