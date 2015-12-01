package sl.com.lib.wirelessdevicecommunication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sl.com.lib.wirelessdevicecommunication.device.SLBluetoothDevice;
import sl.com.lib.wirelessdevicecommunication.interfaces.ISLDeviceChanged;

public class SLDeviceManager {
	private static SLDeviceManager _bridge;
	private List<SLDeviceChannel> _deviceChannels;
	private Context _context;
	private static final int REQUEST_ENABLE_BT = 1;
	private  int _onReceiveCount = 0;
	private ISLDeviceChanged _iSLDeviceChanged;
	public enum Action
	{
		SEND
	}
	public BluetoothAdapter makesureEnableBluetooth()
	{
		BluetoothAdapter mBluetoothAdapter = null;
		try {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null) {
				return null;
			}
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				((Activity)_context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		catch (Exception ex)
		{
			Log.i("shenlong","SLDeviceManager makesureEnableBluetooth : " + ex.getMessage());
		}
		return mBluetoothAdapter;
	}
	private final BroadcastReceiver _receiverBluetooth = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			ISLDevice slDevie = new SLBluetoothDevice(device);
			Log.i("shenlong","SLDeviceManager onReceive : device " + device.getName() + " with action "  + action);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.i("shenlong","SLDeviceManager onReceive : ACTION_FOUND");
			}
			else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				Log.i("shenlong","SLDeviceManager onReceive : ACTION_ACL_CONNECTED");
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			}
			else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
			}
			else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				_iSLDeviceChanged.onDeviceDisconnected(slDevie.getSignature());
				Log.i("shenlong","SLDeviceManager onReceive : ACTION_ACL_DISCONNECTED");
			}
		}
	};
	private void registerBluetoothReceivers()
	{
		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		_context.registerReceiver(_receiverBluetooth, filter1);
		_context.registerReceiver(_receiverBluetooth, filter2);
		_context.registerReceiver(_receiverBluetooth, filter3);
	}

	public void discoverBluetooth(int second)
	{
		this.registerBluetoothReceivers();

		Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, second);
		_context.startActivity(discoverableIntent);
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
	public void setContext(Context ctx, ISLDeviceChanged deviceChanged)
	{
		this._context = ctx;
		this._iSLDeviceChanged = deviceChanged;
	};
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
			Log.i("shenlong","SLDeviceManager connect " + signature);
			SLDeviceChannel channel = getDeviceChannel(signature);
			if (channel != null && channel.getDevice().getSignature() == signature)
			{
				if(channel.isConnected())
				{
					res = 1;
				}
				else {
					res = channel.connect();
					if (channel.isAlive() == false
						&&
							(
								channel.getState() == Thread.State.TERMINATED
								||channel.getState() == Thread.State.NEW
							)
						) {
						if(channel.getState() == Thread.State.TERMINATED)
						{
							channel = new SLDeviceChannel(channel.getDevice(), channel.getHandler());
						}
						channel.start();
					}
				}
			}
			else
			{
				Log.i("shenlong","SLDeviceManager connect " + signature + " channel = null");
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
				channel.interrupt();
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
	public int doAction(int signature, Action action, Object msg) throws Exception
	{
		int res = 0;
		Log.i("shenlong", "SLDeviceManager -> doAction " + Action.SEND.name() + " on device " + signature);
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
