package rover;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import tools.DataHandler;
import tools.DataSource;

public class Webcam implements Runnable {	
	WebcamController wc;
	FrameGrabber grabber0 = new OpenCVFrameGrabber(1);
	//FrameGrabber grabber1 = new OpenCVFrameGrabber(1);
	Frame frame0, frame1;
	boolean active = true;
	
	public Webcam(WebcamController wc) {
		this.wc = wc;
		try {
			grabber0.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			frame0 = grabber0.grab();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
			e1.printStackTrace();
		}
		//frame1 = grabber1.grab();
		if (frame0 != null) {
			wc.inputFrame(0, frame0);
		}
		
	}
	
}
