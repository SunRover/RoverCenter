package rover.comms;

import java.awt.image.BufferedImage;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.ImageIcon;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacv.FrameGrabber;

import rover.DataTypes;
import tools.DataReciever;

public class WebcamServer implements DataReciever {
	private static final String[] REQUESTED_DATA = {DataTypes.DTYPE_WEBCAMIMAGE1};
	
	ServerSocket server;
	Socket client;
	ObjectOutputStream stream1;
	BufferedImage bimg1;
	OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	Java2DFrameConverter paintConverter = new Java2DFrameConverter();
	boolean connected = false;
	
	
	public WebcamServer(int port) {
		try {
			server = new ServerSocket(port);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void connect() {
		if (connected == false) {
			try {
				client = server.accept();
				System.out.println("Got Socket 1");
				stream1 = new ObjectOutputStream(client.getOutputStream());
				connected = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public String[] getDataTypes() {
		return null;
	}

	@Override
	public void recieveData(String arg0, Object arg1) {
		if (arg0.equals(DataTypes.DTYPE_WEBCAMIMAGE1)) {
			Frame frame1 = (Frame) arg1;
			
			try {
				if (frame1 != null) {
					// Flip image horizontally
					//cvFlip(img1, img1, 1);
					// Show video frame in canvas
					bimg1 = paintConverter.getBufferedImage(frame1, 1);
					stream1.writeUnshared(new ImageIcon(bimg1));
					stream1.flush();
					stream1.reset();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("WCS: Reconnecting");
				connected = false;
				connect();
			}
		}
	}
}