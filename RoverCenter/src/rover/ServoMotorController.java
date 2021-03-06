package rover;

import java.util.concurrent.TimeUnit;

import tools.DataHandler;
import tools.DataReciever;

public class ServoMotorController implements DataReciever {
	private static final String[] REQUEST_DATATYPES = {DataTypes.DYTPE_SERVOMOTORVALS};
	private SerialConnection bricktronics;
	private boolean good;
	
	public ServoMotorController(String port) {
		//Make connection
		bricktronics = new SerialConnection(port, 9600, 8, 1, 0);
		//Wait a sec
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (bricktronics.isGood())
			good = true;
	}
	
	//State of ServoMotorController
	public boolean isGood() {
		return good;
	}
	
	//Set the speed of the motor
	public void setSpeed(int speed) {
		byte[] message = {(byte) (speed/2+127)};
		bricktronics.sendMessage(message);
	}
	
	@Override
	public String[] getDataTypes() {
		return REQUEST_DATATYPES;
	}

	@Override
	public void recieveData(String type, Object data) {
		if (type == DataTypes.DYTPE_SERVOMOTORVALS) {
			int speed = (Integer) data;
			setSpeed(speed);
		}
	}

}
