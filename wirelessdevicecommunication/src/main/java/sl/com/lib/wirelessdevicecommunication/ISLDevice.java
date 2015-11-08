package sl.com.lib.wirelessdevicecommunication;

import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;

public interface ISLDevice {
	int send(Object msg) throws Exception;
	int connect() throws Exception;
	int disconnect() throws Exception;
	int close() throws Exception;
	Object receive(Handler handler) throws Exception;
	String getName();
	int getSignature();
	boolean isConnected();
	InputStream getInputStream();
	OutputStream getOutputStream();
}
