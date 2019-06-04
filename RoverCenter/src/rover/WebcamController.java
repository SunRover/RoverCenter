package rover;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.*;

import org.opencv.core.Mat;

import tools.DataHandler;
import tools.DataSource;

public class WebcamController implements DataSource {
	private static final String[] OFFERED_DATA = {DataTypes.DTYPE_WEBCAMIMAGE0, DataTypes.DTYPE_WEBCAMIMAGE1};
	
	DataHandler dh;
	Webcam floorcam;
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	
	public WebcamController (DataHandler dh) {
		this.dh = dh;
		dh.addSource(this);
		floorcam = new Webcam(this);
		executor.scheduleAtFixedRate(floorcam, 1000, 500, MILLISECONDS);
	}

	public void inputFrame(int webcamnum, Mat frame) {
		if (webcamnum == 1) {
			dh.pushData(DataTypes.DTYPE_WEBCAMIMAGE1, frame);
		}
	}
	
	@Override
	public String[] getOfferedDataTypes() {
		return OFFERED_DATA;
	}
}