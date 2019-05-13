package javacv.ch04;

import javacv.Helper;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_imgproc.HISTCMP_INTERSECT;
import static org.bytedeco.opencv.global.opencv_imgproc.compareHist;


/**
 * 使用 `compareHist` 计算图像相似性
 *
 * Created by liulongbiao on 16-6-16.
 */
public class ImageComparator {
    private Mat referenceImage;
    private ColorHistogram hist;
    private Mat referenceHistogram;

    public ImageComparator(Mat referenceImage) {
        this(referenceImage, 8);
    }

    public ImageComparator(Mat referenceImage, int numberOfBins) {
        hist = new ColorHistogram();
        this.referenceImage = referenceImage;
        hist.setNumberOfBins(numberOfBins);
        referenceHistogram = hist.getHistogram(referenceImage);
    }

    public Mat getReferenceImage() {
        return referenceImage;
    }

    /**
     * Compare the reference image with the given input image and return similarity score.
     */
    public double compare(Mat image) {
        Mat inputH = hist.getHistogram(image);
        Helper.show(inputH,"hit");
        Helper.show(referenceHistogram,"hit");
        return compareHist(referenceHistogram, inputH, HISTCMP_INTERSECT);
    }
}
