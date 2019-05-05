package cn.huangsy.video;

import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import static javacv.Helper.show;

/**
 * @author huangsy
 * @date 2019/5/5 9:15
 */
public class VideoTest {

    public static final String M4S = "http://10.0.224.243/live_record/liangc_test_2019_04_17_1_400_444x444_422/playlist.m3u8";
    public static final String TS = "http://10.0.224.19/vod/wwy___3_wmv_4000309_200000_320x240_1734/vod.m3u8";

    public static void main(String[] args) {
        VideoCapture cap = new VideoCapture(TS);
        int i = 0;
        //判断视频是否打开
        if (cap.isOpened()) {
            Mat frame = new Mat();
            while (true) {
                cap.read(frame);
                if (frame.empty()) {
                    break;
                }
                if (i<10){
                    i++;
                    show(frame, "frame_"+i);
                }

            }
            cap.release();
        }
    }
}
