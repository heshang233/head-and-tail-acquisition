package cn.huangsy.javacv;


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
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.swscale.*;

/**
 * @author huangsy
 * @date 2019/4/26 14:33
 */
public class Test {

    public static final String M4S = "http://10.0.224.243/live_record/liangc_test_2019_04_17_1_400_444x444_422/playlist.m3u8";
    public static final String TS = "http://10.0.224.19/vod/wwy___3_wmv_4000309_200000_320x240_1734/vod.m3u8";

    public static void main(String[] args) throws IOException {

        ByteBuffer byteBuffer = grabVideoFrame(TS, AV_PIX_FMT_BGR24);
        System.out.println("--"+byteBuffer);

    }

    /**
     * 打开视频流
     * @param url -url
     * @return
     * @throws FileNotOpenException
     */
    protected static AVFormatContext openInput(String url){
        AVFormatContext pFormatCtx = new AVFormatContext(null);
        if(avformat_open_input(pFormatCtx, url, null, null)==0) {
            return pFormatCtx;
        }
        throw new RuntimeException("Didn't open video file");
    }


    /**
     * 检索流信息（rtsp/rtmp检索时间过长问题解决）
     * @param pFormatCtx
     * @return
     */
    protected static AVFormatContext findStreamInfo(AVFormatContext pFormatCtx) {
        if (avformat_find_stream_info(pFormatCtx, (PointerPointer<?>)null)>= 0) {
            return pFormatCtx;
        }
        throw new RuntimeException("Didn't retrieve stream information");
    }


    /**
     * 获取视频通道
     * @param pFormatCtx
     * @return
     */
    protected static int findVideoStreamIndex(AVFormatContext pFormatCtx) {
        int size=pFormatCtx.nb_streams();
//		System.err.println("流数量："+size);
        for (int i = 0; i < size; i++) {
            AVStream stream=pFormatCtx.streams(i);
            AVCodecContext codec=stream.codec();
            int type=codec.codec_type();
//			System.err.println("类型："+type);
            if (type == AVMEDIA_TYPE_VIDEO) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 指定视频帧位置获取对应视频帧
     * @param pFormatCtx
     * @param videoStream
     * @return
     */
    protected static AVCodecContext findVideoStream(AVFormatContext pFormatCtx, int videoStreamIndex) {
        if(videoStreamIndex >=0) {
            // Get a pointer to the codec context for the video stream
            AVStream stream=pFormatCtx.streams(videoStreamIndex);
            AVCodecContext pCodecCtx = stream.codec();
            return pCodecCtx;
        }
        //如果没找到视频流,抛出异常
        throw new RuntimeException("Didn't open video file");
    }

    /**
     * 查找并尝试打开解码器
     * @return
     */
    protected static AVCodecContext findAndOpenCodec(AVCodecContext pCodecCtx) {
        // Find the decoder for the video stream
        AVCodec pCodec = avcodec_find_decoder(pCodecCtx.codec_id());
        if (pCodec == null) {
            System.err.println("Codec not found!");
            throw new RuntimeException("Codec not found!");
        }
        AVDictionary optionsDict = null;
        // Open codec
        if (avcodec_open2(pCodecCtx, pCodec, optionsDict) < 0) {
            System.err.println("Could not open codec!");
            throw new RuntimeException("Could not open codec!"); // Could not open codec
        }
        return pCodecCtx;
    }

    /**
     * 抓取视频帧（默认跳过音频帧和空帧）
     * @param url
     * @param fmt - 像素格式，比如AV_PIX_FMT_BGR24
     * @return
     * @throws IOException
     */
    public static ByteBuffer grabVideoFrame(String url, int fmt) throws IOException {

        // Open video file
        AVFormatContext pFormatCtx=openInput(url);
        //不再使用减少缓存和检索时长方法，该方法导致高清/高清视频无法获取到i帧的问题
//		if(url.indexOf("rtmp")>=0) {
        //解决rtmp检索时间过长问题
        //限制最大读取缓存，
//		    pFormatCtx.probesize(PROBESIZE);//设置500k能保证高清视频也能读取到关键帧
        //限制avformat_find_stream_info最大持续时长，设置成3秒
//		    pFormatCtx.max_analyze_duration(MAX_ANALYZE_DURATION);
//		}
        // Retrieve stream information
        pFormatCtx=findStreamInfo(pFormatCtx);
        // Dump information about file onto standard error
        //av_dump_format(pFormatCtx, 0, url, 0);

        //Find a video stream
        final int videoStream=findVideoStreamIndex(pFormatCtx);

        AVCodecContext pCodecCtx =findVideoStream(pFormatCtx,videoStream);

        // Find the decoder for the video stream
        pCodecCtx= findAndOpenCodec(pCodecCtx);
        // Allocate video frame
        AVFrame pFrame = av_frame_alloc();
        //Allocate an AVFrame structure
        AVFrame pFrameRGB = av_frame_alloc();

        int srcWidth = pCodecCtx.width();
        int srcHeight = pCodecCtx.height();

        pFrameRGB.width(srcWidth);
        pFrameRGB.height(srcHeight);
        pFrameRGB.format(fmt);

        // Determine required buffer size and allocate buffer
        int numBytes = avpicture_get_size(fmt, srcWidth, srcHeight);
        DoublePointer param=null;
        SwsContext sws_ctx = sws_getContext(srcWidth, srcHeight, pCodecCtx.pix_fmt(), srcWidth, srcHeight,fmt, SWS_FAST_BILINEAR, null, null, param);

        BytePointer buffer = new BytePointer(av_malloc(numBytes));
        // Assign appropriate parts of buffer to image planes in pFrameRGB
        // Note that pFrameRGB is an AVFrame, but AVFrame is a superset
        // of AVPicture
        avpicture_fill(new AVPicture(pFrameRGB), buffer, fmt, srcWidth, srcHeight);
        AVPacket packet = new AVPacket();
        int[] frameFinished = new int[1];
        try {
            while (av_read_frame(pFormatCtx, packet) >= 0) {
                // Is this a packet from the video stream?
                if (packet.stream_index() == videoStream) {
                    //Is i frame?
                    if(packet.flags()==AV_PKT_FLAG_KEY) {
                        // Decode video frame
                        avcodec_decode_video2(pCodecCtx, pFrame, frameFinished, packet);
                        // Did we get a video frame?
                        if (frameFinished[0] >= 0) {
                            // Convert the image from its native format to BGR
                            sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0, srcHeight, pFrameRGB.data(),pFrameRGB.linesize());
                            //Convert BGR to ByteBuffer
                            return saveFrame(pFrameRGB, srcWidth, srcHeight);
                        }
                    }
                }
                // Free the packet that was allocated by av_read_frame
                av_free_packet(packet);
            }
            //读取错误或读取完成
            return null;
        }finally {
//			av_free(buffer);//Don't free buffer
            av_free_packet(packet);// Free the packet that was allocated by av_read_frame
            av_free(pFrameRGB);// Free the RGB image
            av_free(pFrame);// Free the YUV frame
            sws_freeContext(sws_ctx);//Free SwsContext
            avcodec_close(pCodecCtx);// Close the codec
            avformat_close_input(pFormatCtx);// Close the video file
        }
    }

    protected static ByteBuffer saveFrame(AVFrame pFrame, int width, int height){
        BytePointer data = pFrame.data(0);
        int size = width * height * 3;
        ByteBuffer buf = data.position(0).limit(size).asBuffer();
        return buf;
    }
}
