/* SerialConnection
 * Makes a serial connection to a port and allows strings to be sent and recieved
 */

package rover;

import jssc.SerialPort; import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class SerialConnection {
	
	SerialPort serialPort;
	boolean good = true;
	
	public SerialConnection(String portname, int baud, int data, int stop, int parity) {
		//Try to open port
		serialPort = new SerialPort(portname);
		try {
			serialPort.openPort();
			serialPort.setParams(baud, data, stop, parity);
		} catch (SerialPortException ex) {
			good = false;
		}
	}
	
	//State of connection
	public boolean isGood() {
		return good;
	}
	
	//Send a string, returns state
	public boolean sendMessage(byte[] s) {
		try {
			serialPort.writeBytes(s);
		} catch (SerialPortException e) {
			good = false;
		}
		return isGood();
	}
	
	//Read a string, should check state afterwards
	public byte[] readMessage() {
		byte[] s;
		try {
			//s = serialPort.readString(9, 10000);
			s = serialPort.readBytes(8, 10000);
		} catch (SerialPortException e) {
			good = false;
			s = null;
		} catch (SerialPortTimeoutException e) {
			s = null;
		}
		return s;
	}
	
	//Close the serial connection
	public boolean close() {
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			return false;
		}
		
		return true;
	}

}