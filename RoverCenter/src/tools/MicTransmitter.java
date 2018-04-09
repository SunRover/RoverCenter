package tools;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;                                                                                                                                             
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;


public class MicTransmitter implements Runnable {

	int threadToRun = 0;
	int bufferSize;
	byte[] buffer1;
	byte[] buffer2;
	int count1;
	int count2;
	int available;
	int readcount;
	boolean running;
	boolean recording;
	TargetDataLine line;
	final AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
	//final AudioFormat format = new AudioFormat(8000.0f, 8, 1, true, true);
	OutputStream outputStream;
	boolean bufferFull = false;
	boolean keyPressed = false;

	public MicTransmitter(IOStreamPack io) {
		try {
			bufferSize = (int) format.getSampleRate() * format.getFrameSize();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, bufferSize);
			line.start();
			buffer1 = new byte[bufferSize];
			buffer2 = new byte[bufferSize];
			outputStream = new BufferedOutputStream(io.getOutputStream());
			Thread t = new Thread(this);
			threadToRun = 0;
			t.start();
			running = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while (running) {
				available = line.available();
				if (available == 0)
					continue;
				
				readcount = line.read(buffer1, 0, available);
				if (readcount == -1) {
					running = false;
				}
				else if (readcount > 0) {
					outputStream.write(buffer1, 0, available);
					//System.out.println("MICTRANSMITTER: Wrote data");
				}
			}
			
			/*
			if (threadToRun == 0) {
				threadToRun = 1;
				while (running) {
					if (bufferFull == false) {
						count1 = line.read(buffer1, 0, buffer1.length);
						buffer2 = buffer1;
						count2 = count1;
						buffer1 = new byte[buffer2.length];
						count1 = 0;
						bufferFull = true;
					}
				}
			}
			else if (threadToRun == 1){
				while (running) {
					if (bufferFull == true) {
						if (recording == true) {
							System.out.println("Writing out...");
							outputStream.write(buffer2, 0, count2);
						}
						bufferFull = false;
					}
				}
			}
			outputStream.write(buffer1, 0, count2);
			buffer1 = new byte[bufferSize];
			buffer2 = new byte[bufferSize];*/
		} catch (IOException e) {
			System.exit(-1);
			System.out.println("exit");
		} catch (Exception e) {
			System.out.println("Direct Upload Error");
			e.printStackTrace();
		}
	}
}
