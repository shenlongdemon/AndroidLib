package sl.com.lib.wirelessdevicecommunication.device;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import sl.com.lib.wirelessdevicecommunication.ISLDevice;

public class SLBluetoothDevice implements ISLDevice {
	private BluetoothDevice _bluetoothDevice;
	private BluetoothSocket _bluetoothSocket = null;
	private InputStream _inputStream;
	private OutputStream _outputStream;
	private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private int _signature;

	public SLBluetoothDevice(BluetoothDevice device)
	{
		_bluetoothDevice = device;	
		int signature = _bluetoothDevice.hashCode();
		setSignature(signature);
	}
	
	@Override
	public int connect() throws Exception{
		// TODO Auto-generated method stub
		int res = 0;
		if(_bluetoothDevice != null)
		{
			try
			{
				Log.i("shenlong", "SLBluetoothDevice - Begin create bluetooth socket");
				if(_bluetoothSocket == null) {
					_bluetoothSocket = _bluetoothDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);
				}
				if(_bluetoothSocket.isConnected() == false)
				{
					_bluetoothSocket.connect();
				}

				_inputStream = _bluetoothSocket.getInputStream();
				_outputStream = _bluetoothSocket.getOutputStream();
				res = 1;
				Log.i("shenlong", "SLBluetoothDevice - Create bluetooth socket successfully");
			}
			catch(Exception ex)
			{
				Log.i("shenlong", "SLBluetoothDevice -> connect() -> " + ex.getMessage());
				throw new Exception("\nSLBluetoothDevice -> connect() -> " + ex.getMessage() , ex);
			}
		}
		return res;
	}
	@Override
	public boolean isConnected()
	{
		boolean isConn = false;
		if(_bluetoothDevice != null)
		{
			if(_bluetoothSocket != null &&_bluetoothSocket.isConnected() == true)
			{
				isConn = true;
			}
		}
		return isConn;
	}

	@Override
	public int send(Object msg) throws Exception {
		// TODO Auto-generated method stub
		int res = 0;
		byte[] msgBuffer = null;
		if(msg instanceof  byte[])
		{
			msgBuffer = (byte[])msg;
		}
		else
		{
			msgBuffer = msg.toString().getBytes();
		}
		try {
			Log.i("shenlong", "SLBluetoothDevice - send(" + msg + ")");
			_outputStream.write(msgBuffer); // write bytes over BT connection
			res = 1;
			// via outstream
		} catch (IOException ex) {
			throw new Exception("\nSLBluetoothDevice -> send("
									+ msg + ") -> " + ex.getMessage()
					+ "\n-> cannot send to " + _bluetoothDevice.getName()  , ex);
		}
		return res;
	}
	@Override
	public Object receive(Handler handler) throws Exception{
		// TODO Auto-generated method stub
		byte[] buffer = new byte[10240];
		int bytes;
		try
		{
			bytes = _inputStream.read(buffer); // read bytes from input
			if(handler != null) {
				handler.obtainMessage(1, bytes, this.getSignature(), buffer).sendToTarget();
			}
		}
		catch(Exception ex)
		{
			throw new Exception("\nSLBluetoothDevice -> recieve() -> " + ex.getMessage() , ex);
		}
		return buffer;
	}
	@Override
	public int close() throws Exception {
		// TODO Auto-generated method stub
		int res = 1;
		if(_bluetoothSocket != null)
		{
			try
			{
				_bluetoothSocket.close();
				_bluetoothSocket = null;
				_inputStream = null;
				_outputStream = null;
			}
			catch(Exception ex)
			{
				throw new Exception("\nSLBluetoothDevice -> disconnect() -> " + ex.getMessage() + "\n-> Cannot disconnect " + this.getName()  , ex);

			}
		}
		return res;
	}
	public int disconnect() throws Exception {
		try {
			return this.close();
		}
		catch (Exception ex)
		{
			throw new Exception("\nSLBluetoothDevice -> disconnect() -> " + ex.getMessage() , ex);
		}
	}
	public String getName()
	{
		String name = "Undentify_Device";
		if(_bluetoothDevice != null)
		{
			name = _bluetoothDevice.getName();
		}
		return name;
	}
	public void setSignature(int signature)
	{
		_signature = signature;
	}
	public int getSignature()
	{
		return _signature;
	}
	public InputStream getInputStream(){return _inputStream;}
	public OutputStream getOutputStream(){return _outputStream;}
}
