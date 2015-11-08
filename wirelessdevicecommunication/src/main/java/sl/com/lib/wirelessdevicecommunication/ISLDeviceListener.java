package sl.com.lib.wirelessdevicecommunication;

/**
 * Created by shenlong on 9/23/2015.
 */
public interface ISLDeviceListener {
    void onDeviceReceived(ISLDevice fromDevice, Object data );

}
