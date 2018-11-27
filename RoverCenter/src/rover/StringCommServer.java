package rover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tools.DataHandler;
import tools.DataSource;
import tools.IOStreamPack;
import tools.StateHolder;
import tools.StateListener;

public class StringCommServer extends Thread implements DataSource, StateListener, StateHolder {
	
	private static final String[] OFFERED_TYPES = {DataTypes.DTYPE_COMMANDERSTRING};

	
	Server server;
	PrintWriter out;
	BufferedReader in;
	volatile IOStreamPack iopack;
	volatile Queue<String> buffer = new LinkedList<String>();
	List<StateListener> listeners = new ArrayList<StateListener>();
	boolean running = true;
	volatile boolean good = false;
	private DataHandler dh;
	
	public StringCommServer(int port, DataHandler dh) {
		iopack = new IOStreamPack();
		server = new Server(port, iopack);
		server.addStateListener(this);
		this.dh = dh;
	}
	
	//State of commserver
	boolean isGood() {
		return good;
	}
	
	//Send a string to the client
	public void sendMessage(String s) {
		out.println(s);
	}
	
	//Receive a string over the connection
	public String getMessage() {
		String line = null;
		
		try {
			line = in.readLine();
		} catch (IOException e) {
			good = false;
			e.printStackTrace();
		}
				
		return line;
	}
	
	//Read a line from the buffer
	public String readLine() {
		//System.out.println("STRCOM: Line reqested");
		
		String line;
		
		synchronized(buffer)  {
			line = buffer.poll();
		}
		
		return line;
	}
	
	public String[] getOfferedDataTypes() {
		return OFFERED_TYPES;
	}
	
	public void run() {
		String line;
		
		while (running) {						
						
			if (good) {
				System.out.println("STRCOM: trying to recieve message");
				line = getMessage();
				if (line != null) {
					dh.pushData("DTYPE_COMMANDERSTRING", line);
					System.out.println("STRCOM: " + "recieved \"" + line + "\"");
				}
				
				System.out.flush();
			}
		}
	}

	public void close() {
		server.close();
	}

	@Override
	public void updateState(boolean state) {
		System.out.println("STRCOM: got state update");
		if (state == false) {
			good = false;
			System.out.println("STRCOM: not running");
		}
		else {
			System.out.println("HEY");
			System.out.println("STRCOM: iopack: " + Boolean.toString(iopack.getInputStream()!=null));
			System.out.flush();
			in = new BufferedReader(new InputStreamReader(iopack.getInputStream()));
			out = new PrintWriter(iopack.getOutputStream());
			good = true;
		}
	}

	@Override
	public void addStateListener(StateListener listener) {
		listeners.add(listener);
	}
}
