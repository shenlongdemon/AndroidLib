package sl.com.lib.wirelessdevicecommunication;

import android.os.Handler;
import android.util.Log;

public class SLDeviceChannel extends Thread {
	private ISLDevice _device;
	private Handler _handler;
	public SLDeviceChannel(ISLDevice device, Handler handler) throws Exception
	{
		try
		{
			_device = device;
			_handler = handler;
		}
		catch(Exception ex)
		{
			throw new Exception(ex.getMessage() + "\nDeviceCommunication -> SLDeviceChannel(ISLDevice) -> "
					+ ex.getMessage() + "\n-> Cannot install SLDeviceChannel for " + this.getDeviceName() , ex);
		}
	}
	public ISLDevice getDevice(){return _device;}
	public Handler getHandler(){return _handler;}
	public String getDeviceName()
	{
		if(_device != null)
		{
			return _device.getName();
		}
		else
		{
			return "Device is null";
		}
	}
	public boolean isConnected()
	{
		boolean isConn = false;
		if(_device.isConnected())// && this.isAlive() == true)
		{
			isConn = true;
		}
		return isConn;
	}
	public final void run() {
		try {

			// Keep looping to listen for received messages
			while (true) {
				Object msg = _device.receive(_handler);
			}
		}
		catch (Exception ex)
		{
			Log.i("shenlong", "Error when receive " + ex.getMessage());
		}
	}
	public int send(Object msg) throws Exception
	{
		int res = 0;
		res = _device.send(msg);
		return res;
	}
	public boolean hasSignature(int signature)
	{
		boolean isDevice = false;
		
		int deviceSignature = _device.getSignature();
		if(deviceSignature == signature)
		{
			isDevice = true;
		}
		
		return isDevice;
	}
	public int disconnect() throws Exception
	{
		try
		{
			return _device.disconnect();
		}
		catch (Exception ex)
		{
			throw new Exception("\nSLDeviceChannel -> disconnect() -> " + ex.getMessage() , ex);
		}

	}
	public int connect() throws Exception
	{
		try
		{
			return _device.connect();
		}
		catch (Exception ex)
		{
			throw new Exception("\nSLDeviceChannel -> connect() -> " + ex.getMessage() , ex);
		}

	}
}
