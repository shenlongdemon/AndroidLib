package sl.com.lib.wirelessdevicecommunication;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SLDeviceManager {
	private static SLDeviceManager _bridge;
	private List<SLDeviceChannel> _deviceChannels;
	public enum Action 
	{
		SEND
	}
	public static SLDeviceManager getInstance()
	{
		if(_bridge == null)
		{
			_bridge = new SLDeviceManager();
			
		}
		return _bridge;
	}
	private SLDeviceManager()
	{
		_deviceChannels = new ArrayList<SLDeviceChannel>();
	}
	
	public List<ISLDevice> getDevices()
	{
		List<ISLDevice> devices = new ArrayList<ISLDevice>();

		for (SLDeviceChannel channel : _deviceChannels) {
			devices.add(channel.getDevice());
		}
		return devices;
	}
	public boolean isConnected(int signature)
	{
		boolean res = false;
		SLDeviceChannel channel = getDeviceChannel(signature);
		if(channel != null)
		{
			res = channel.isConnected();
		}
		return res;
	}
	public int connect(int signature) throws Exception {
		int res = 0;
		try {
			SLDeviceChannel channel = getDeviceChannel(signature);
			if (channel != null && channel.getDevice().getSignature() == signature)
			{
				if(channel.isConnected())
				{
					res = 1;
				}
				else {
					res = channel.connect();
					if (channel.isAlive() == false && channel.getState() == Thread.State.NEW) {
						channel.start();
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw new Exception("\nSLDeviceManager -> connect(signature) -> " + ex.getMessage(), ex);
		}
		return res;
	}
	public int disconnect(int signature) throws Exception {
		int res = 0;
		try {
			boolean isExist = false;
			SLDeviceChannel channel = getDeviceChannel(signature);
			if (channel != null && channel.getDevice().getSignature() == signature)
			{
				res = channel.disconnect();
			}
		}
		catch (Exception ex)
		{
			throw new Exception("\nSLDeviceManager -> disconnect(signature) -> " + ex.getMessage(), ex);
		}
		return res;
	}
	public int manage(ISLDevice device, Handler handler) throws Exception
	{
		int res = 0;
		try
		{
			SLDeviceChannel channel = getDeviceChannel(device.getSignature());
			if(channel == null) {
				SLDeviceChannel chan = new SLDeviceChannel(device, handler);
				_deviceChannels.add(chan);
			}
			res = 1;
		}
		catch(Exception ex)
		{
			throw new Exception("\nSLDeviceManager -> manage(ISLDevice, Handler) -> " + ex.getMessage(), ex);
		}
		return res;
	}
	private SLDeviceChannel getDeviceChannel(int signature)
	{
		SLDeviceChannel commRes = null;
		for(SLDeviceChannel comm : _deviceChannels)
		{				
			if(comm.hasSignature(signature) == true)
			{
				commRes = comm;
				break;
			}
		}
		return commRes;
	}

	public int disconnect() throws Exception
	{
		try {
			for(SLDeviceChannel comm : _deviceChannels)
			{
				comm.disconnect();
			}
			return 1;
		}
		catch (Exception ex)
		{
			Log.i("shenlong", "Error when disconnect to a communication");
			throw new Exception("SLDeviceManager -> Error when disconnect", ex);
		}

	}
	public int doAction(int signature, Action action, Object msg) throws Exception
	{
		int res = 0;
		Log.i("shenlong", "SLDeviceManager -> doAction " + Action.SEND.name());
		if(action == Action.SEND)
		{

			SLDeviceChannel comm = getDeviceChannel(signature);
			if(comm != null)
			{
				comm.send(msg);
				res = 1;
			}
		}
		
		
		return res;
	}
}
