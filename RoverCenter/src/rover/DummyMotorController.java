package rover;

import tools.DataReciever;

public class DummyMotorController implements DataReciever {

	private static final String[] REQUESTED_DATA = {DataTypes.DTYPE_MOTORVALS};
	
	@Override
	public String[] getDataTypes() {
		return REQUESTED_DATA;
	}

	@Override
	public void recieveData(String arg0, Object arg1) {
		System.out.println("DUMMYMC: data recieved");
		if (arg0.equals(DataTypes.DTYPE_MOTORVALS)) {
			byte[][] command = (byte[][]) arg1;
			System.out.println("Front: " + command[0][1] + " " + command[0][0]);
			System.out.println("Back : " + command[1][1] + " " + command[1][0]);
		}
	}	
}
