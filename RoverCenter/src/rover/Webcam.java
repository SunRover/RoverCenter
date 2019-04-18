package rover;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Webcam implements Runnable {	
	WebcamController wc;
	VideoCapture vcapture1;
	Mat frame1 = new Mat();
	boolean active = true;
	
	public Webcam(WebcamController wc) {
		this.wc = wc;
		vcapture1 = new VideoCapture(1);
	}

	@Override
	public void run() {
		if (active && vcapture1.isOpened()) {
			vcapture1.read(frame1);
			
			if (frame1 != null) {
					wc.inputFrame(1, frame1);
			}
			else {
				System.out.println("Frame read null");
			}
		}

		else {
			System.out.println("Webcam unconnected");
		}
	}
}
