package rover;

import java.util.ArrayList;

public class MasterDriver {
	MotorController mc;
	ArrayList<Driver> drivers = new ArrayList<Driver>();
	
	public MasterDriver(MotorController mc) {
		this.mc = mc;
	}
	
	public void addDriver(Driver driver) {
		drivers.add(driver);
	}
}