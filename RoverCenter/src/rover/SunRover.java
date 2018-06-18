/* SunRover
 * Main execution thread for the on-board SunRover brain computer
 */

package rover;

import rover.comms.ServerAudioHandler;
import rover.comms.WebcamServer;
import tools.DataHandler;
import tools.DataReciever;

public class SunRover implements DataReciever {
	public static final int CONTROL_PORT = 1300;
	public static final int WEBCAM_PORT = 1301;
	public static final int AUDIO_PORT = 1302;
	
	private static final String[] REQUESTED_DATA = {"DTYPE_COMMANDERSTRING"};
	
	DataHandler dh;
	MotorController mc;
	ServoController sc;
	ServoMotorController sm;
	StringCommServer commserver;
	Driver driver;
	WebcamServer ws;
	ServerAudioHandler sa;
	boolean done = false;
	
	public SunRover() {
		dh = new DataHandler();
		mc = new MotorController();
		sc = new ServoController("COM7");
		sm = new ServoMotorController("COM8");
		commserver = new StringCommServer(1300, dh);
		driver = new DirectionDriver(dh);
		ws = new WebcamServer(WEBCAM_PORT);
		sa = new ServerAudioHandler(AUDIO_PORT);
		
		dh.addSource(commserver);
		dh.addSource(driver);
		dh.addReciever(driver);
		dh.addReciever(mc);
		dh.addReciever(sc);
		dh.addReciever(sm);
		
		if (mc.isGood())
			System.out.println("Connected to arduino motorcontrollers");
		if (sc.isGood())
			System.out.println("Connected to maestro");
		if (sm.isGood())
			System.out.println("Connected to servomotor controller");
		
		commserver.start();
		
		while (!done) {
		}
		
		System.out.print("Closing");
		
		mc.close();
		commserver.close();
	}
	
	public static void main(String[] args) {		
		new SunRover();
	}

	@Override
	public String[] getDataTypes() {
		return REQUESTED_DATA;
	}

	@Override
	public void recieveData(String type, Object data) {
		if (type.equals("DTYPE_COMMANDERSTRING")) {
			if (data.equals("q")) {
				done = true;
			}
		}
	}
}
