package rover;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import tools.DataHandler;
import tools.DataReciever;
import tools.DataSource;

public class PositionAnalyzer implements Runnable, DataReciever, DataSource {
	protected static final String[] REQUESTED_DATA = {DataTypes.DTYPE_WEBCAMIMAGE1};
	protected static final String[] OFFERED_DATA = {DataTypes.DTYPE_POSITION};
	
	protected static final int HISTOGRAM_RES = 12;
	
	protected volatile boolean active = true, needToProcess = false;
	protected Mat lastframe = new Mat(), preprocess = new Mat();
	protected Mat lines = new Mat();
	protected int[] angleHistogram = new int[HISTOGRAM_RES];
	protected Point pt1 = new Point(), pt2 = new Point();
	
	protected Scalar linecolor = new Scalar(100, 255, 100);	//Draw lines color 1
	protected Scalar linecolor2 = new Scalar(255, 0, 0);	//Draw lines color 2

	public PositionAnalyzer(DataHandler dh) {
		dh.addReciever(this);
		
		HighGui.namedWindow("Position Analyzer");
		
		Thread t = new Thread(this);
		t.start();
	}
	
	
	@Override
	public void run() {
		while (active) {
			if (needToProcess) {
				processImage(lastframe);
			}
		}
	}

	private void processImage(Mat frame) {
		if (frame == null) {
			System.out.println("Frame null");
			return;
		}
		
		System.out.println("Analyzing frame");
		
		//Preprocess image (color -> grayscale -> canny edge)
		Imgproc.cvtColor(frame, preprocess, Imgproc.COLOR_BGR2GRAY);
		//Imgproc.blur(preprocess, preprocess, new Size(2, 2));
		Imgproc.Canny(preprocess, preprocess, 40, 70, 3, true);
        
        //Perform Probabilistic Hough Transform
        Imgproc.HoughLinesP(preprocess, lines, 1, Math.PI / 180, 100, 50, 10);
        
        
        for (int li = 0; li < lines.rows(); li++) {
        	double[] line = lines.get(li, 0);
        	
        	//Put data into points
        	pt1.x = line[0]; pt1.y = line[1];
        	pt2.x = line[2]; pt2.y = line[3];   	
            
            //Make line vectors pointing upwards (note opencv starts counting y from top)
            if (pt1.y < pt2.y) {
            	Point temp = pt1;
            	pt1 = pt2;
            	pt2 = temp;
            }
            
            double xdiff = pt2.x-pt1.x; double ydiff = pt1.y - pt2.y;
            double tandev = xdiff/ydiff;	//Tangent of deviation angle from vertical (heading)
            double angle = Math.atan(tandev);
            
            int histindex = (int) (HISTOGRAM_RES*angle/(2*Math.PI)) + HISTOGRAM_RES/2;
            
            angleHistogram[histindex]++;
            
            Imgproc.line(preprocess, pt1, pt2, linecolor, 2); // draw the segment on the image
        }
        
        
        int strongestAngle = 0;
        int secondStrongestAngle = 0;
        int thirdStrongestAngle = 0;
        for (int i = 0; i < HISTOGRAM_RES; i++) {
        	if (angleHistogram[i] > angleHistogram[strongestAngle]) {
        		thirdStrongestAngle = secondStrongestAngle;
        		secondStrongestAngle = strongestAngle;
        		strongestAngle = i;
        	}
        	else if (angleHistogram[i] > angleHistogram[secondStrongestAngle]) {
        		thirdStrongestAngle = secondStrongestAngle;
        		secondStrongestAngle = i;
        	}
        	else if (angleHistogram[i] > angleHistogram[thirdStrongestAngle]) {
        		thirdStrongestAngle = i;
        	}
        }
        
        System.out.println(strongestAngle + " " + secondStrongestAngle + " " + thirdStrongestAngle);
        System.out.println(angleHistogram[strongestAngle] + " " + angleHistogram[secondStrongestAngle] + " " + angleHistogram[thirdStrongestAngle]);
        
        /*There is a grid if all true:
         * There are non-zero-strength first and second angles
         * The two strongest angles are perpendicular to each other
         * The third strongest angle is no closer to the second than the second is to the first (both second and first must be much more defined than third)
         */
        if (angleHistogram[strongestAngle]!=0 && angleHistogram[secondStrongestAngle]!=0
        		&& ((strongestAngle+HISTOGRAM_RES/2)%HISTOGRAM_RES - secondStrongestAngle < 1)
        		&& (!(angleHistogram[thirdStrongestAngle]!=0 && (angleHistogram[secondStrongestAngle]-angleHistogram[thirdStrongestAngle] < angleHistogram[strongestAngle]-angleHistogram[secondStrongestAngle])))) {
        	System.out.println("Wooh! Found a grid!");
        	pt1.x = 0;
        	pt1.y = 0;
        	pt2.y = Math.sin(strongestAngle*Math.PI/HISTOGRAM_RES)*100;
        	pt2.x = Math.cos(strongestAngle*Math.PI/HISTOGRAM_RES)*100;
        	Imgproc.line(preprocess, pt1, pt2, linecolor2, 10); // draw the segment on the image
        }
        
        HighGui.imshow("Position Analyzer", preprocess);
        HighGui.waitKey(50);
        
        Arrays.fill(angleHistogram, 0);
	}

	@Override
	public String[] getOfferedDataTypes() {
		return OFFERED_DATA;
	}

	@Override
	public String[] getDataTypes() {
		return REQUESTED_DATA;
	}

	@Override
	public void recieveData(String arg0, Object arg1) {
		if (arg0.equals(DataTypes.DTYPE_WEBCAMIMAGE1)) {
			Mat f = (Mat) arg1;
			f.copyTo(lastframe);
			needToProcess = true;
		}
	}

}
