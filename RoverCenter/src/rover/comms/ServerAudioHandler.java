package rover.comms;

import rover.Server;
import tools.AudioPlayer;
import tools.IOStreamPack;
import tools.MicTransmitter;
import tools.StateListener;

public class ServerAudioHandler implements StateListener {

	Server server;
	IOStreamPack io = new IOStreamPack();
	MicTransmitter mt;
	AudioPlayer ap;
	
	public ServerAudioHandler (int port) {
		server = new Server(port, io);
		server.addStateListener(this);
		System.out.println("SAH: Made server");
	}

	@Override
	public void updateState(boolean state) {
		if (state == true) {
			System.out.println("SAH: Server good. Making AudioPlayer");			
			ap = new AudioPlayer(io);
			mt = new MicTransmitter(io);
		}
	}
}
