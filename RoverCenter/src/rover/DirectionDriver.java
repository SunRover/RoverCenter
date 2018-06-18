/*DirectionDriver
 * Creates messages for MotorController from direction commands
 */

package rover;

import java.util.Arrays;

import tools.DataHandler;

public class DirectionDriver extends Driver{
	byte[][] command = new byte[2][2];
	
	public DirectionDriver(DataHandler dh) {
		super(dh);
	}

	public void inputCommand(String s) {
		System.out.println(s);
		
		if (s.startsWith("MOTOR_")) {
			Arrays.fill(command[0], (byte) 1);
			Arrays.fill(command[1], (byte) 1);
			
			if (s.equals("MOTOR_UP")) {
				//mc.go(127);
				command[0][0] = command[0][1] = (byte) 127;
				command[1] = command[0];
			}
			else if (s.equals("MOTOR_DOWN")) {
				command[0][0] = command[0][1] = (byte) -127;
				command[1] = command[0];
			}
			else if (s.equals("MOTOR_LEFT")) {
				command[0][0] = (byte) 127;
				command[0][1] = (byte) -command[0][0];
				command[1] = command[0];
			}
			else if (s.equals("MOTOR_RIGHT")) {
				command[0][0] = (byte) -127;
				command[0][1] = (byte) -command[0][0];
				command[1] = command[0];
			}
			else if (s.equals("MOTOR_STOP")) {
				command[0][0] = command[0][1] = (byte) 0;
				command[1] = command[0];
			}
			
			sendMotorVals(command);
		}
		
		else if (s.startsWith("SERVO_")) {
			if (s.equals("SERVO_UP")) {
				sendServoVals(0, 1800);
			}
			else if (s.equals("SERVO_DOWN")) {
				sendServoVals(0, 1000);
			}
		}
		
		else if (s.startsWith("SERVOMOTOR_")) {
			if (s.equals("SERVOMOTOR_FORWARD"))
				sendServoMotorVals(255);
			else if (s.equals("SERVOMOTOR_BACKWARD"))
				sendServoMotorVals(-255);
			else if (s.equals("SERVOMOTOR_STOP"))
				sendServoMotorVals(0);
		}
	}
	
}
