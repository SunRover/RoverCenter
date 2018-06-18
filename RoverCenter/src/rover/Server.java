/* Server
 * Serves a socket to connect to SunRover
 */

package rover;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import tools.IOStreamPack;
import tools.StateHolder;
import tools.StateListener;

public class Server implements Runnable, StateHolder {
		
	ServerSocket serversocket;
	Socket clientsocket;
	volatile IOStreamPack iopack;
	List<StateListener> statelisteners = new ArrayList<StateListener>();
	volatile boolean good = false;
	
	public Server(int port, IOStreamPack iopack) {
		//Try to open a port and wait until connected
		try {
			serversocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			good = false;
		}
		
		this.iopack = iopack;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	//State of connection
	public boolean isGood() {
		System.out.println(good);
		return good;
	}
	
	//Close the connection and port
	public boolean close() {
		try {
			clientsocket.close();
			serversocket.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	//Wait for input
	public void run() {		
		while (clientsocket == null || clientsocket.isClosed()) {
			setState(false);
			try {
				clientsocket = serversocket.accept();
				System.out.println(iopack == null);
				iopack.setInputStream(clientsocket.getInputStream());
				iopack.setOutputStream(clientsocket.getOutputStream());
				setState(true);
				System.out.println("Connected");
			} catch (IOException e) {
				System.out.println("SERVER: Error connecting to client");
				e.printStackTrace();
			}
		}
	}

	private void setState(boolean state) {
		if (good != state) {
			good = state;
		
			for (StateListener listener: statelisteners) {
				System.out.println("State change");
				listener.updateState(isGood());
			}
		}
	}
	
	@Override
	public void addStateListener(StateListener listener) {
		statelisteners.add(listener);
	}
}