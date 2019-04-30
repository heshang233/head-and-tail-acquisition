package cn.huangsy.javacv;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.Point;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author huangsy
 * @date 2019/4/29 10:31
 */
public class VideoTest {

    public static void getSplitedPicture(String dirPath, String fileName) {
        Mat image = Imgcodecs.imread(dirPath + fileName);
        Mat imageBackup = image.clone();
        //灰度
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        //二值
        double[] data = image.get(0, 0);
        int thres = (int)(data[0] - 5);
        Imgproc.threshold(image, image, thres, 255, Imgproc.THRESH_BINARY);
        //反色
        Core.bitwise_not(image, image);
        //检测轮廓
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        //查找最大区域
        double maxArea = 0;
        int maxAreaIndex = -1;
        for(int i=0; i<contours.size(); i++)
        {
            double tmparea = Math.abs(Imgproc.contourArea(contours.get(i)));
            if(tmparea > maxArea)
            {
                maxArea = tmparea;
                maxAreaIndex = i;
            }
        }
        //裁剪
        if (maxAreaIndex > 0) {
            Rect rect = Imgproc.boundingRect(contours.get(maxAreaIndex));
            Mat result = imageBackup.submat(rect);
            Imgcodecs.imwrite(dirPath + fileName.substring(0, fileName.lastIndexOf(".")) + "_result.jpg", result);
        }
    }




    private static final int ALPHA_SLIDER_MAX = 100;
    private int alphaVal = 0;
    private Mat matImgSrc1;
    private Mat matImgSrc2;
    private Mat matImgDst = new Mat();
    private JFrame frame;
    private JLabel imgLabel;

    public VideoTest(){}

    public VideoTest(String[] args) {
        //! [load]
        String imagePath1 = "D:\\huangsy\\opencv\\sources\\samples\\data\\LinuxLogo.jpg";
        String imagePath2 = "D:\\huangsy\\opencv\\sources\\samples\\data\\WindowsLogo.jpg";
        if (args.length > 1) {
            imagePath1 = args[0];
            imagePath2 = args[1];
        }
        matImgSrc1 = Imgcodecs.imread(imagePath1);
        matImgSrc2 = Imgcodecs.imread(imagePath2);
        //! [load]
        if (matImgSrc1.empty()) {
            System.out.println("Empty image: " + imagePath1);
            System.exit(0);
        }
        if (matImgSrc2.empty()) {
            System.out.println("Empty image: " + imagePath2);
            System.exit(0);
        }

        //! [window]
        // Create and set up the window.
        frame = new JFrame("Linear Blend");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(matImgSrc2);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        //! [window]
    }

    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));

        //! [create_trackbar]
        sliderPanel.add(new JLabel(String.format("Alpha x %d", ALPHA_SLIDER_MAX)));
        JSlider slider = new JSlider(0, ALPHA_SLIDER_MAX, 0);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        //! [create_trackbar]
        //! [on_trackbar]
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                alphaVal = source.getValue();
                update();
            }
        });
        //! [on_trackbar]
        sliderPanel.add(slider);

        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    public static double compare_image(Mat mat_1, Mat mat_2) {

        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //颜色范围
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        //直方图大小， 越大匹配越精确 (越慢)
        MatOfInt histSize = new MatOfInt(1000);

        Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

        // CORREL 相关系数
        double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
        return res;
    }

    // "D:\\ib\\face-detact\\src\\com\\company\\a1.jpg"
    private static Mat conv_Mat(String img_1) {
        Mat image0 = Imgcodecs.imread(img_1);

        Mat image = new Mat();
        //灰度转换
        Imgproc.cvtColor(image0, image, Imgproc.COLOR_BGR2GRAY);

        return image;
    }

    private void update() {
        double alpha = alphaVal / (double) ALPHA_SLIDER_MAX;
        double beta = 1.0 - alpha;
        Core.addWeighted(matImgSrc1, alpha, matImgSrc2, beta, 0, matImgDst);
        Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

    public void run(String[] args) {
        String input = args.length > 0 ? args[0] : "D:\\BaiduNetdiskDownload\\S02E04.mp4";
        boolean useMOG2 = args.length > 1 ? args[1] == "MOG2" : true;

        //! [create]
        BackgroundSubtractor backSub;
        if (useMOG2) {
            backSub = Video.createBackgroundSubtractorMOG2();
        } else {
            backSub = Video.createBackgroundSubtractorKNN();
        }
        //! [create]

        //! [capture]
        VideoCapture capture = new VideoCapture(input);
        if (!capture.isOpened()) {
            System.err.println("Unable to open: " + input);
            System.exit(0);
        }
        //! [capture]

        Mat frame = new Mat(), fgMask = new Mat();
        int time = 0;
        Instant start = Instant.now();
        System.out.println(start);
        while (true) {
            capture.read(frame);
            if (frame.empty()) {
                break;
            }


            //! [apply]
            // update the background model
            backSub.apply(frame, fgMask);
            //! [apply]


            //! [display_frame_number]
            // get the frame number and write it on the current frame
            Imgproc.rectangle(frame, new org.opencv.core.Point(10, 2), new org.opencv.core.Point(100, 20), new Scalar(255, 255, 255), -1);
            String frameNumberString = String.format("%d", (int)capture.get(Videoio.CAP_PROP_POS_FRAMES));
            Imgproc.putText(frame, frameNumberString, new org.opencv.core.Point(15, 15), 0, 0.5,
                    new Scalar(0, 0, 0));
            //! [display_frame_number]

            //! [show]
            // show the current frame and the fg masks
//            HighGui.imshow("Frame", frame);
//            HighGui.imshow("FG Mask", fgMask);

            Mat mat_1 = new Mat();
            //灰度转换
            Imgproc.cvtColor(frame, mat_1, Imgproc.COLOR_BGR2GRAY);

            Mat mat_2 = conv_Mat("D:\\home\\photo\\s3.png");

            double compareHist = compare_image(mat_1, mat_2);

            if (compareHist > 0.98) {
                Instant cut = Instant.now();
                System.out.println(cut);
                System.out.println(compareHist+"\"匹配, the duration is \""+ Duration.between(start, cut).getSeconds());
                break;
            }

            //! [show]

            // get the input from the keyboard
//            int keyboard = HighGui.waitKey(30);
//            if (keyboard == 'q' || keyboard == 27) {
//                break;
//            }
        }

//        HighGui.waitKey();
        System.exit(0);
    }

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new VideoTest().run(args);

    }
}
