/* SunRover
 * Main execution thread for the on-board SunRover brain computer
 */

package rover;

import java.util.Scanner;

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
	WebcamController wc;
	boolean done = false;
	
	Scanner stdin = new Scanner(System.in);
	
	public SunRover() {
		dh = new DataHandler();
		//mc = new MotorController();
		//sc = new ServoController("/dev/ttyACM0");
		//sm = new ServoMotorController("COM5");
		commserver = new StringCommServer(1300, dh);
		driver = new LineDriver(dh);
		ws = new WebcamServer(WEBCAM_PORT);
		//sa = new ServerAudioHandler(AUDIO_PORT);
		wc = new WebcamController(dh);
		boolean done = false;

		dh.addSource(commserver);
		dh.addSource(wc);
		dh.addSource(driver);
		dh.addReciever(driver);
		dh.addReciever(mc);
		//dh.addReciever(sc);
		//dh.addReciever(sm);
		dh.addReciever(new DummyMotorController());
		
		commserver.start();
		
		
		if (mc.isGood())
			System.out.println("Connected to arduino motorcontrollers");
		/*ss
		if (sc.isGood())
			System.out.println("Connected to maestro");
		if (sm.isGood())
			System.out.println("Connected to servomotor controller");
		*/
		
		while (!done) {
			String input;
			
			if (commserver.isGood()) {
				input = commserver.readLine();
				input = stdin.nextLine();
				
				System.out.println(input);
				
				if (input != null) {
					dh.pushData(DataTypes.DTYPE_COMMANDERSTRING, input);
				}
			}
		}
		
		System.out.print("Closing");
		
		mc.close();
		commserver.close();
	}
	
	public static void main(String[] args) {		
		new SunRover();
	}

	public String[] getDataTypes() {
		return REQUESTED_DATA;
	}

	public void recieveData(String type, Object data) {
		if (type.equals("DTYPE_COMMANDERSTRING")) {
			if (data.equals("q")) {
				done = true;
			}
		}
	}
}
