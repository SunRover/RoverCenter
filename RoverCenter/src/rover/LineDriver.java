/*Floor Driver
 * Drives straight based on looking at the floor tiles
 */

package rover;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import tools.DataHandler;
import tools.DataReciever;

public class LineDriver extends Driver implements Runnable {
	private static final String[] REQUESTED_DATA = {DataTypes.DTYPE_COMMANDERSTRING, DataTypes.DTYPE_WEBCAMIMAGE0};
	private static final int NUMGROUPS   = 5;
	
	boolean active = true;
	Frame lastframe = null;
	IplImage src, dst, colorDst;
    OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
    CvMemStorage storage = cvCreateMemStorage(0);
    CvSeq lines;
    CanvasFrame hough = new CanvasFrame("Hough");
	byte[][] command = new byte[2][2];
	float targetavgxint = -1;
	
	List<List<CvPoint>> linegroups = new ArrayList<List<CvPoint>>();
	List<CvPoint> vertGroundLines = new ArrayList<CvPoint>();
	List<CvPoint> oldVertGroundLines = new ArrayList<CvPoint>();
	
	public LineDriver(DataHandler dh) {
		super(dh);
		
		/*
		for (int i = 0; i < NUMGROUPS; i++) {
			linegroups.add(new ArrayList<CvPoint>());
		}
		*/
		
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public String[] getDataTypes() {
		return REQUESTED_DATA;
	}

	@Override
	public void recieveData(String arg0, Object arg1) {
		if (arg0.equals(DataTypes.DTYPE_COMMANDERSTRING)) {
			String command = (String) arg1;
			if (command.equals("MOTOR_STOP")) {
				active = false;
			}
			else if (command.equals("MOTOR_UP")) {
				active = true;
			}
		}
		
		else if (arg0.equals(DataTypes.DTYPE_WEBCAMIMAGE0)) {
			Frame f = (Frame) arg1;
			if (lastframe == null) {
				initializeOpenCV(f);
			}
			lastframe = f;
		}
	}

	@Override
	public void run() {
		System.out.println("Running");
		while (true) {
			if (active) {
				processFrame(lastframe);
			}
		}
	}

	private void initializeOpenCV(Frame f) {
		src = iplConverter.convert(f);
		
		dst = cvCreateImage(cvGetSize(src), src.depth(), 1);
        colorDst = cvCreateImage(cvGetSize(src), src.depth(), 3);
	}
	
	private void processFrame(Frame frame) {
		//System.out.println("Processing frame");
		
		if (frame == null) {
			System.out.println("Frame null");
			return;
		}
		
		src = iplConverter.convert(frame);
		
		cvCanny(src, dst, 50, 200, 3);
        cvCvtColor(dst, colorDst, CV_GRAY2BGR);
        
        //Probabilistic Hough Transform
        lines = cvHoughLines2(dst, storage, CV_HOUGH_PROBABILISTIC, 1, Math.PI / 180, 150, 100, 150, 0, CV_PI);
        
        float sum_deviance = 0;
        float sumxint = 0;
        
        for (int li = 0; li < lines.total(); li++) {
            Pointer line = cvGetSeqElem(lines, li);
            CvPoint pt1  = new CvPoint(line).position(0);
            CvPoint pt2  = new CvPoint(line).position(1);
            CvPoint s, e;
            
            //Make line vectors pointing upwards (note opencv starts counting y from top)
            if (pt1.y() > pt2.y()) {
            	s = pt1;
            	e = pt2;
            }
            else {
            	s = pt2;
            	e = pt1;
            }
            
            float xdiff = e.x()-s.x();
            float tandev = xdiff == 0? 0 : xdiff/(s.y()-e.y());	//Tangent of deviation angle from vertical
            
            //Remove extremes
            if (Math.abs(tandev) > 0.5) {
            	cvSeqRemove(lines, li);
            	li--;
            }
            else {
            	/*
                System.out.println("Line spotted: ");
                System.out.println("\t pt1: " + pt1);
                System.out.println("\t pt2: " + pt2);*/
                cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0); // draw the segment on the image
                
                sum_deviance += tandev;
                
                //Find x-intercepts
                CvPoint xint = new CvPoint();
                xint.x((int) (e.x()+(e.y()*tandev)));
                xint.y(0);
                cvCircle(colorDst, xint, 10, CV_RGB(255, 0, 0));
                
                //Sort lines
                boolean sorted = false;
                for (List<CvPoint> group : linegroups) {
                	if (Math.abs(group.get(0).x() - xint.x()) < 50) {
                		group.add(xint);
                		sorted = true;
                	}
                }
                
                //If part of new group, place in order left to right in linegroups list
                if (!sorted) {
                	List<CvPoint> newgroup = new ArrayList<CvPoint>();
                	newgroup.add(xint);
                	/*
                	int i;
                	for (i = 0; i < linegroups.size(); i++) {
                		if (xint.x() < linegroups.get(i).get(0).x())
                			linegroups.add(i, newgroup);
                	}*/
                	linegroups.add(newgroup);
                }
                
                //linegroups.get(NUMGROUPS*(int)((xint.x()-1)/dst.width())).add(xint);
            }
        }
        
        
        //If can't see anything don't do anything
        if (lines.total() == 0) {
           	//Do nothing
        	return;
        }
        
        //Consolidate line groups into lines
        for (List<CvPoint> linegroup : linegroups) {
        	CvPoint avgline = new CvPoint();
        	for (CvPoint line : linegroup) {
        		avgline.x(avgline.x()+line.x());
        	}
        	avgline.x(avgline.x()/linegroup.size());
        	vertGroundLines.add(avgline);
            cvCircle(colorDst, avgline, 10, CV_RGB(0, 255, 0));
        }
        
        //Map lines onto old lines and find difference
        float offset = 0;
        int nummatches = 0;
        for (CvPoint line : vertGroundLines) {
        	for (CvPoint oldline : oldVertGroundLines) {
        		float diff = line.x() - oldline.x();
        		if (Math.abs(diff) < 100) {
        			offset += diff;
        			nummatches++;
        		}
        	}
        }
        offset /= nummatches;
        
        if (oldVertGroundLines.size() == 0 && vertGroundLines.size() > 0)
        	for (CvPoint line : vertGroundLines)
        		oldVertGroundLines.add(line);
        
        //System.out.println("LD: " + vertGroundLines.size());
        if (offset != 0 && nummatches != 0)
        	System.out.println("LD: Offset " + offset);
        
        float correction = -sum_deviance/lines.total();
        
        float avgxint = sumxint/lines.total();
        float xintcorrection = targetavgxint - avgxint;
        
        if (targetavgxint == -1) {
        	targetavgxint = avgxint;
        }
        else if (offset > 0) {
        	command[0][0] = (byte) 127;
        	command[0][1] = (byte) (127-offset*10);
        	command[1] = command[0];
        }
        else {
        	command[0][0] = (byte) (127+offset*10);
        	command[0][1] = (byte) 127;
        	command[1] = command[0];
        }
        
        hough.showImage(iplConverter.convert(colorDst));
        
        sendMotorVals(command);
        
        vertGroundLines.clear();
        linegroups.clear();
	}
}
