/* SunRover
 * Main execution thread for the on-board SunRover brain computer
 */

package rover;

import java.util.Scanner;

import rover.comms.ServerAudioHandler;
import rover.comms.WebcamServer;
import tools.DataHandler;

public class SunRover {
	public static final int CONTROL_PORT = 1300;
	public static final int WEBCAM_PORT = 1301;
	public static final int AUDIO_PORT = 1302;
	
	DataHandler dh;
	MotorController mc;
	ServoController sc;
	ServoMotorController sm;
	StringCommServer commserver;
	Driver driver;
	WebcamServer ws;
	ServerAudioHandler sa;
	
	Scanner stdin = new Scanner(System.in);
	
	public SunRover() {
		dh = new DataHandler();
		//mc = new MotorController();
		sc = new ServoController("/dev/ttyACM0");
		//sm = new ServoMotorController("COM5");
		commserver = new StringCommServer(1300);
		driver = new DirectionDriver(dh);
		//ws = new WebcamServer(WEBCAM_PORT);
		//sa = new ServerAudioHandler(AUDIO_PORT);
		boolean done = false;
		
		dh.addSource(commserver);
		dh.addSource(driver);
		dh.addReciever(driver);
		//dh.addReciever(mc);
		dh.addReciever(sc);
		//dh.addReciever(sm);
		
		commserver.start();
		
		
		if (sc.isGood())
			System.out.println("Connected to maestro");
		/*
		if (sm.isGood())
			System.out.println("Connected to servomotor controller");
		*/
		
		while (!done) {
			String input;
			
			//if (commserver.isGood()) {
				//input = commserver.readLine();
				input = stdin.nextLine();
				
				System.out.println(input);
				
				if (input != null) {
					dh.pushData(DataTypes.DTYPE_COMMANDERSTRING, input);
				}
			//}
		}
		
		System.out.print("Closing");
		
		mc.close();
		commserver.close();
	}
	
	public static void main(String[] args) {		
		new SunRover();
	}
}
