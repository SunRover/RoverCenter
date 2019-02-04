package rover;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import tools.DataHandler;
import tools.DataSource;

public class WebcamController implements Runnable, DataSource {
	private static final String[] OFFERED_DATA = {DataTypes.DTYPE_WEBCAMIMAGE0, DataTypes.DTYPE_WEBCAMIMAGE1};
	
	DataHandler dh;
	FrameGrabber grabber0 = new OpenCVFrameGrabber(1);
	//FrameGrabber grabber1 = new OpenCVFrameGrabber(1);
	Frame frame0, frame1;
	boolean active = true;
	
	public WebcamController(DataHandler dh) {
		this.dh = dh;
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public String[] getOfferedDataTypes() {
		return OFFERED_DATA;
	}

	@Override
	public void run() {
		while (active) {
			try {
				// Start grabbers to capture video
				grabber0.start();
				//grabber1.start();
				while (true) {
					frame0 = grabber0.grab();
					//frame1 = grabber1.grab();
					if (frame0 != null) {
						dh.pushData(DataTypes.DTYPE_WEBCAMIMAGE0, frame0);
					}
					if (frame1 != null) {
						dh.pushData(DataTypes.DTYPE_WEBCAMIMAGE1, frame1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
