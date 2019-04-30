package cn.huangsy.video;

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

import java.nio.ByteBuffer;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avcodec.av_free_packet;
import static org.bytedeco.ffmpeg.global.avcodec.avcodec_close;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.avutil.av_free;
import static org.bytedeco.ffmpeg.global.swscale.*;

/**
 * @author huangsy
 * @date 2019/4/30 8:55
 */
public class Test {

    public static final String M4S = "http://10.0.224.243/live_record/liangc_test_2019_04_17_1_400_444x444_422/playlist.m3u8";
    public static final String TS = "http://10.0.224.19/vod/wwy___3_wmv_4000309_200000_320x240_1734/vod.m3u8";

    public static void main(String[] args) {
        AVFormatContext avFormatContext = findStreamInfo(openInput(TS));

        final int videoStream=findVideoStreamIndex(avFormatContext);

        AVCodecContext pCodecCtx =findVideoStream(avFormatContext,videoStream);

        pCodecCtx= findAndOpenCodec(pCodecCtx);

        // Allocate video frame
        AVFrame pFrame = av_frame_alloc();
        //Allocate an AVFrame structure
        AVFrame pFrameRGB = av_frame_alloc();

        int srcWidth = pCodecCtx.width();
        int srcHeight = pCodecCtx.height();

        pFrameRGB.width(srcWidth);
        pFrameRGB.height(srcHeight);
        pFrameRGB.format(AV_PIX_FMT_BGR24);

        // Determine required buffer size and allocate buffer
        int numBytes = av_image_get_buffer_size(AV_PIX_FMT_BGR24, srcWidth, srcHeight, 1);


        DoublePointer param=null;
        SwsContext sws_ctx = sws_getContext(srcWidth, srcHeight, pCodecCtx.pix_fmt(), srcWidth, srcHeight,AV_PIX_FMT_BGR24, SWS_FAST_BILINEAR, null, null, param);

        BytePointer buffer = new BytePointer(av_malloc(numBytes));
        // Assign appropriate parts of buffer to image planes in pFrameRGB
        // Note that pFrameRGB is an AVFrame, but AVFrame is a superset
        // of AVPicture
        AVFrame avFrame = new AVFrame(pFrameRGB);
        av_image_fill_arrays(new PointerPointer(avFrame), avFrame.linesize(), buffer, AV_PIX_FMT_BGR24, srcWidth, srcHeight, 1);
        AVPacket packet = new AVPacket();
        int[] frameFinished = new int[1];
        try {
            while (av_read_frame(avFormatContext, packet) >= 0) {
                // Is this a packet from the video stream?
                if (packet.stream_index() == videoStream) {
                    //Is i frame?
                    if(packet.flags()==AV_PKT_FLAG_KEY) {
                        // Decode video frame
                        avcodec_send_packet(pCodecCtx, packet);
                        // Did we get a video frame?
                        if (frameFinished[0] >= 0) {
                            // Convert the image from its native format to BGR
                            sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0, srcHeight, pFrameRGB.data(),pFrameRGB.linesize());
                            //Convert BGR to ByteBuffer
                            saveFrame(pFrameRGB, srcWidth, srcHeight);
                        }
                    }
                }
                // Free the packet that was allocated by av_read_frame
                av_packet_unref(packet);
            }
        }finally {
//			av_free(buffer);//Don't free buffer
            av_packet_unref(packet);// Free the packet that was allocated by av_read_frame
            av_free(pFrameRGB);// Free the RGB image
            av_free(pFrame);// Free the YUV frame
            sws_freeContext(sws_ctx);//Free SwsContext
            avcodec_close(pCodecCtx);// Close the codec
            avformat_close_input(avFormatContext);// Close the video file
        }
    }

    protected static ByteBuffer saveFrame(AVFrame pFrame, int width, int height){
        BytePointer data = pFrame.data(0);
        int size = width * height * 3;
        ByteBuffer buf = data.position(0).limit(size).asBuffer();
        return buf;
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
}
