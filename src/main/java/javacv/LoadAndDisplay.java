/**
 * 
 */
package javacv;


import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

/**
 * @author liulongbiao
 *
 */
public class LoadAndDisplay {

	static final String WIN_NAME = "Display window";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String imgName = "a15.jpg";
		Mat image = imread(Helper.getResourcePath(imgName));
		if (image.empty()) {
			throw new RuntimeException("cannot find img " + imgName + " in classpath");
		}
		namedWindow(WIN_NAME, WINDOW_AUTOSIZE); // Create a window for display.
		imshow(WIN_NAME, image); // Show our image inside it.
		waitKey(0); // Wait for a keystroke in the window
	}

}
