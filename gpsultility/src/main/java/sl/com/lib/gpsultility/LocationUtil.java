package sl.com.lib.gpsultility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by shenlong on 03/03/2016.
 */
public class LocationUtil {
    private GoogleApiClient mGoogleApiClient;
    private Geocoder mGeocoder;
    private LocationRequest mLocationRequest;
    private boolean isLocationSuccess = false;
    private Context mContext;
    private LocationListener mLocationListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallback;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedCallback;

    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;


    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final String TAG = "shenlong";
    public static final float MIN_ACCURACY = 25.0f;
    public static final float MIN_LAST_READ_ACCURACY = 500.0f;
    public static final long ONE_MIN = 1000 * 60;
    public static final long TWO_MIN = ONE_MIN * 2;
    public static final long FIVE_MIN = ONE_MIN * 5;
    public LocationUtil(Context context, LocationListener locationListener, GoogleApiClient.ConnectionCallbacks connectionCallbacks
            , GoogleApiClient.OnConnectionFailedListener connectionFailedListener){
        this.mContext = context;
        this.mLocationListener = locationListener;
        this.mConnectionCallback = connectionCallbacks;
        this.mConnectionFailedCallback = connectionFailedListener;
        initialize();
    }

    protected synchronized void initialize(){

        Log.i(TAG, "LocationUtil initialize");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        this.mGoogleApiClient = new GoogleApiClient
                .Builder(mContext)
                .addApi(Plus.API)
                .addApi(LocationServices.API)
                //.addApi(Drive.API)
                //.addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this.mConnectionCallback)
                .addOnConnectionFailedListener(this.mConnectionFailedCallback)
                .build();
        this.mGeocoder = new Geocoder(mContext, Locale.getDefault());
        Log.i(TAG, "LocationUtil initialize-ed");
    }

    public GoogleApiClient getGoogleApiClient(){
        return this.mGoogleApiClient;
    }

    public void connect(){
        Log.i(TAG, "LocationUtil connect");
        mGoogleApiClient.connect();
        Log.i(TAG, "LocationUtil connected");
    }
    public boolean isConnected(){
        return mGoogleApiClient.isConnected();
    }
    public void disconnect(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            Log.e("Google disconnect", "=====");
            mGoogleApiClient.disconnect();
        }

    }

    public void makeLocationRequest() {
        Log.i(TAG, "LocationUtil makeLocationRequest");

        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        Log.i(TAG, "LocationUtil makeLocationRequest-ed");
    }
    public void removeLocationUpdates(){
        fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }

    public void retrieveLocationName(final Location location, final LocationCallback callback){
        if (location != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<Address> addressList = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addressList != null && addressList.size() > 0) {
                            final String locationName = addressList.get(0).getLocality();
                            Log.e("PLACE NAME", location.getLatitude() + "===" + location.getLongitude() + "==========" + locationName);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (locationName != null && !locationName.isEmpty()){
                                        //CommonVariables.currentPlaceName = locationName;
                                        callback.onDone(true);
                                    }
                                    else {
                                        callback.onDone(false);
                                    }
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Error", "Retrieve address list is error");
                    }
                }
            });
            thread.start();
        }
    }

    public interface LocationCallback{
        void onDone(boolean isSuccess);
    }
    public LatLng getLatLngOfCamera(GoogleMap map)
    {
        LatLng centerOFCamera = map.getCameraPosition().target;
        return centerOFCamera;
    }
    public Location getLocation(LatLng latLng)
    {
        Location location = new Location("getLocation");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return  location;
    }
    public LatLng getLatLng(Location location)
    {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        return latLng;
    }
    public Location getLastLocation() {
        Log.i(TAG, "LocationUtil getLastLocation");
        Location location = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
        return location;
    }

    public String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }



}
