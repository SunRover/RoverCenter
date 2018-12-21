/*Driver
 * Abstract class that passes motor controller instructions
 * TODO: Add basic emergency functionality for manual control to abstract driver so it is present in all drivers
 */

package rover;

import tools.DataHandler;
import tools.DataReciever;
import tools.DataSource;

public abstract class Driver implements DataSource, DataReciever {
	private static final String[] OFFERED_DATA = {DataTypes.DTYPE_MOTORVALS, DataTypes.DTYPE_SERVOVALS, DataTypes.DYTPE_SERVOMOTORVALS};
	
	private DataHandler datahandler;
		
	public Driver(DataHandler dh) {
		datahandler = dh;
	}
	
	//Put out values for movement motors
	protected void sendMotorVals(byte[][] motorvals) {
		if (motorvals.length == 2 && motorvals[0].length == 2 && motorvals[1].length == 2)
			datahandler.pushData(DataTypes.DTYPE_MOTORVALS, motorvals);
	}
	
	//Put out values for servos
	protected void sendServoVals(int channel, int pulse) {
		int[] data = {0, channel, pulse};
		
		if (channel >= 0 && channel < 6 && pulse >=0 && pulse < 6001) {
			datahandler.pushData(DataTypes.DTYPE_SERVOVALS, (Object) data);	
		}
	}
	
	//Put out values for servomotors
	protected void sendServoMotorVals(int speed) {
		if (speed < 256 && speed > -256) {
			datahandler.pushData(DataTypes.DYTPE_SERVOMOTORVALS, speed);
		}
	}

	public String[] getOfferedDataTypes() {
		return OFFERED_DATA;
	}
	
}
