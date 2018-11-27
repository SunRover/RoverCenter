package rover;

import java.util.concurrent.TimeUnit;

import tools.DataHandler;
import tools.DataReciever;

public class ServoController implements DataReciever {
	
	public static final int SERVO_SETTARGET = 0;
	public static final int SERVO_GETMOVINGSTATE = 1;
	
	private static final String[] REQUESTED_DATA = {DataTypes.DTYPE_SERVOVALS};
	
	SerialConnection maestro;	//Connection to Polulu Mini-Maestro
	boolean good = false;	//State of connection
	
	public ServoController(String port) {		
		//Make connection
		maestro = new SerialConnection(port, 9600, 8, 1, 0);
		//Wait a sec
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (maestro.isGood())
			good = true;
	}
	
	//Make command to set certain pulse on a channel and send command
	private void setTarget(int channel, int pulse) {
		pulse *= 4;		//Convert to 
		
		byte[] command = new byte[4];
		command[0] = (byte) 0x84;										//Command byte
		command[1] = (byte) channel;									//Channel byte
		command[2] = (byte) (((short) pulse) & 0b0000000001111111);		//Low bits of pulse
		command[3] = (byte) (((short) pulse) >> 7);						//High bits of pulse
		
		System.out.println("SERVOCONTROLLER: message****");
		System.out.println(Integer.toBinaryString((command[0] & 0xFF) + 0x100).substring(1));
		System.out.println(Integer.toBinaryString((command[1] & 0xFF) + 0x100).substring(1));
		System.out.println(Integer.toBinaryString((command[2] & 0xFF) + 0x100).substring(1));
		System.out.println(Integer.toBinaryString((command[3] & 0xFF) + 0x100).substring(1));
		System.out.println("****");
		
		sendCommand(command);
	}
	
	//Get weather any servos are moving
	private boolean getMovingState() {
		byte[] command = {(byte) 0x93};
		sendCommand(command);
		return getResponse()[0] == 1;
	}
	
	//Send command to the maestro
	private void sendCommand(byte[] command) {
		maestro.sendMessage(command);
	}
	
	//Get a response from the maestro
	private byte[] getResponse() {
		return maestro.readMessage();
	}
	
	//State of ServoController
	public boolean isGood() {
		return good;
	}
	
	//Give data to receive
	public String[] getDataTypes() {
		return REQUESTED_DATA;
	}

	public void recieveData(String type, Object data) {
		int[] vals = (int[]) data;
		
		//vals[0] = SERVO_GETMOVINGSTATE;
		
		System.out.println("SERVOCONTROLLER: recieved command");
		
		if (vals[0] == SERVO_SETTARGET) {
			System.out.println("SERVOCONTROLLER: Setting target " + vals[1] + " " + vals[2]);
			setTarget(vals[1], vals[2]);
		}
		else if (vals[0] == SERVO_GETMOVINGSTATE) {
			System.out.println("SERVOCONTROLLER: Getting moving state");
			System.out.println(getMovingState());
		}
	}

}
