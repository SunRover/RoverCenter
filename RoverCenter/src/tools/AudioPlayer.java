package tools;

import java.io.InputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


public class AudioPlayer implements Runnable {
	
	byte[] buffer1 = new byte[16000];
	boolean running = true;
	boolean bufferToPlay = false;
	IOStreamPack io;
	
	public AudioPlayer(IOStreamPack io) {
		try {
			Thread t = new Thread(this);
			t.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		this.io = io;
	}
	
	public void run() {
		try {
			System.out.println("AUDIOPLAYER: Setting up");
			AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
			AudioInputStream ais = new AudioInputStream(io.getInputStream(), format, buffer1.length / format.getFrameSize());
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine sline = (SourceDataLine)AudioSystem.getLine(info);		
			sline.open(format);
			sline.start();
			int nBytesRead = 0;
			while (running == true) {
				while (nBytesRead != -1) {
					nBytesRead = 0;
					System.out.println("AUDIOPLAYER: Trying to read data");
					System.out.println("Inside Loop " + nBytesRead);
					nBytesRead = ais.read(buffer1,0,buffer1.length);
					System.out.println("After Read " + nBytesRead);
					if (nBytesRead == 0)
						System.out.println("AUDIOPLAYER: Read nothing");
					if (nBytesRead > 0) {
						sline.write(buffer1, 0, nBytesRead);
						System.out.println("AUDIOPLAYER: Wrote output");
					}
				}
				nBytesRead = 0;
				ais = new AudioInputStream(io.getInputStream(), format, buffer1.length / format.getFrameSize());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

}
