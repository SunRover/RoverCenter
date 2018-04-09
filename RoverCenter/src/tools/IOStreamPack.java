package tools;

import java.io.InputStream;
import java.io.OutputStream;

public class IOStreamPack {
	private InputStream is;
	private OutputStream os;
	
	
	public IOStreamPack() {
		this.is = null;
		this.os = null;
	}
	public IOStreamPack(InputStream istream, OutputStream ostream) {
		this.is = istream;
		this.os = ostream;
	}
	public InputStream getInputStream() {
		return is;
	}
	public void setInputStream(InputStream is) {
		this.is = is;
	}
	public OutputStream getOutputStream() {
		return os;
	}
	public void setOutputStream(OutputStream os) {
		this.os = os;
	}
}
