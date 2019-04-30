/**
 * 
 */
package javacv;

import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameConverter;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_core.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.round;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


/**
 * @author liulongbiao
 *
 */
public class Helper {

	/**
	 * 获取图片资源路径地址
	 * 
	 * @param img
	 * @return
	 */
	public static String getResourcePath(String img) {
		return Helper.class.getResource("/" + img).getPath();
	}

	/**
	 * 以彩色空间加载图片
	 *
	 * @param file
	 * @return
	 * @throws IOException
     */
	public static Mat load(File file) throws IOException {
		return load(file, IMREAD_COLOR);
	}

	/**
	 * 加载图片
	 *
	 * @param file
	 * @param flags
	 * @return
	 * @throws IOException
     */
	public static Mat load(File file, int flags) throws IOException {
		if(!file.exists()) {
			throw new FileNotFoundException("Image file does not exist: " + file.getAbsolutePath());
		}
		Mat image = imread(file.getAbsolutePath(), flags);
		if(image == null || image.empty()) {
			throw new IOException("Couldn't load image: " + file.getAbsolutePath());
		}
		return image;
	}


	/**
	 * 显示图片
	 *
	 * @param image
	 * @param caption
	 */
	public static void show(Mat image, String caption) {
		CanvasFrame canvas = new CanvasFrame(caption, 1);
		canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		FrameConverter<Mat> converter = new ToMat();
		canvas.showImage(converter.convert(image));
	}

	/**
	 * 显示图片
	 *
	 * @param image
	 * @param caption
     */
	public static void show(Image image, String caption) {
		CanvasFrame canvas = new CanvasFrame(caption, 1);
		canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		canvas.showImage(image);
	}

	/**
	 * 在图像的指定点画红色的圆
	 */
	public static Mat drawOnImage(Mat image, Point2fVector points) {
		Mat dest = image.clone();
		int radius = 5;
		Scalar red = new Scalar(0, 0, 255, 0);
		for (int i = 0; i < points.size(); i++) {
			Point2f p = points.get(i);
			circle(dest, new Point(round(p.x()), round(p.y())), radius, red);
		}

		return dest;
	}

	/**
	 * 在图像上画一个形状
	 * @param image
	 * @param overlay
	 * @param color
     * @return
     */
	public static Mat drawOnImage(Mat image, Rect overlay, Scalar color) {
		Mat dest = image.clone();
		rectangle(dest, overlay, color);
		return dest;
	}

	/**
	 * 保存图片
	 *
	 * @param file
	 * @param image
     */
	public static void save(File file, Mat image) {
		imwrite(file.getAbsolutePath(), image);
	}

	/**
	 * 将 Mat 转换为 BufferedImage
	 *
	 * @param mat
	 * @return
     */
	public static BufferedImage toBufferedImage(Mat mat) {
		ToMat openCVConverter = new ToMat();
		Java2DFrameConverter java2DConverter = new Java2DFrameConverter();
		return java2DConverter.convert(openCVConverter.convert(mat));
	}

	public static Mat toMat8U(Mat src) {
		return toMat8U(src, true);
	}

	/**
	 * Convert `Mat` to one where pixels are represented as 8 bit unsigned integers (`CV_8U`).
	 * It creates a copy of the input image.
	 *
	 * @param src input image.
	 * @return copy of the input with pixels values represented as 8 bit unsigned integers.
	 */
	public static Mat toMat8U(Mat src, boolean doScaling) {
		double[] min = {Double.MAX_VALUE};
		double[] max = {Double.MIN_VALUE};
		minMaxLoc(src, min, max, null, null, new Mat());
		double scale = 1d;
		double offset = 0d;
		if (doScaling) {
			double s = 255d / (max[0] - min[0]);
			scale = s;
			offset = -min[0] * s;
		}

		Mat dest = new Mat();
		src.convertTo(dest, CV_8U, scale, offset);
		return dest;
	}

	/** Convert native vector to JVM array.
	 *
	 * @param matches pointer to a native vector containing DMatches.
	 * @return
	 */
	public static DMatch[] toArray(DMatchVector matches) {
		assert matches.size() <= Integer.MAX_VALUE;
		// for the simplicity of the implementation we will assume that number of key points is within Int range.
		int n = (int) matches.size();

		// Convert keyPoints to Scala sequence
		DMatch[] result = new DMatch[n];
		for (int i = 0; i < n; i++) {
			result[i] = new DMatch(matches.get(i));
		}
		return result;
	}

	/**
	 * 将点的列表转换为 Mat
	 *
	 * @param points
	 * @return
     */
	public static Mat toMatPoint3f(ArrayList<Point3f> points) {
		// Create Mat representing a vector of Points3f
		int sz = points.size();
		Mat dest = new Mat(1, sz, CV_32FC3);
		FloatIndexer indx = dest.createIndexer();
		for (int i = 0; i < sz; i++) {
			Point3f p = points.get(i);
			indx.put(0, i, 0, p.x());
			indx.put(0, i, 1, p.y());
			indx.put(0, i, 2, p.z());
		}
		return dest;
	}
}
