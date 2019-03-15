package rover;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.*;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import tools.DataHandler;
import tools.DataSource;

public class WebcamController implements DataSource {
	private static final String[] OFFERED_DATA = {DataTypes.DTYPE_WEBCAMIMAGE0, DataTypes.DTYPE_WEBCAMIMAGE1};
	
	DataHandler dh;
	Webcam floorcam;
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	
	public WebcamController (DataHandler dh) {
		this.dh = dh;
		floorcam = new Webcam(this);
		executor.scheduleAtFixedRate(floorcam, 1000, 500, MILLISECONDS);
	}

	public void inputFrame(int webcamnum, Frame frame) {
		if (webcamnum == 0) {
			dh.pushData(DataTypes.DTYPE_WEBCAMIMAGE0, frame);
		}
	}
	
	@Override
	public String[] getOfferedDataTypes() {
		return OFFERED_DATA;
	}
}