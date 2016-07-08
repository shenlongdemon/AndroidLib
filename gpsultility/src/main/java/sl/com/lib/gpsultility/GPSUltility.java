package sl.com.lib.gpsultility;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by shenlong on 8/26/2015.
 */





public class GPSUltility implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static  final  String TAG = "shenlong";
    private static  final  String TITLE = "GPSUltility";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected GoogleApiClient _googleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest _locationRequest;
    private Context _context;
    private Activity _activity;
    private ILocationListener _iLocationListener;
    private FusedLocationProviderApi _fusedLocationProviderApi;
    /**
     * Represents a geographical location.
     */
    protected Location _currentLocation;

    public GPSUltility(Context context, Activity activity, ILocationListener iLocationListener) {
        this._context = context;
        this._activity = activity;
        this._iLocationListener = iLocationListener;
        _fusedLocationProviderApi = LocationServices.FusedLocationApi;
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
    }
    public Location getCurrentLocation()
    {
        Log.i(TAG, TITLE + " _googleApiClient is connect :  " + _googleApiClient.isConnected());
        if(_currentLocation == null)
        {
            _currentLocation = _fusedLocationProviderApi
                    .getLastLocation(_googleApiClient);
        }
        return _currentLocation;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, TITLE + " buildGoogleApiClient ");
        createLocationRequest();
        _googleApiClient = new GoogleApiClient.Builder(this._context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        //_googleApiClient.connect();
        Log.i(TAG, TITLE + " buildGoogleApiClient Done");

    }
    protected void createLocationRequest() {
        Log.i(TAG, TITLE + " createLocationRequest");
        _locationRequest = new LocationRequest();
        _locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        _locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.i(TAG, TITLE + " createLocationRequest Done");
    }
    protected void startLocationUpdates() {
        _fusedLocationProviderApi.requestLocationUpdates(_googleApiClient, _locationRequest, this);
    }

    public void onStartGoogleApiClient() {
        Log.i(TAG, TITLE + " onStartGoogleApiClient");
        if(_googleApiClient != null) {
            _googleApiClient.connect();
        }
    }
    public void onResumeGoogleApiClient() {
        if (_googleApiClient != null && _googleApiClient.isConnected())
        {
            startLocationUpdates();
        }
    }
    public void onPauseGoogleApiClient() {
        if (_googleApiClient != null && _googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }


    public void onStopGoogleApiClient() {
        Log.i(TAG, TITLE + " onStopGoogleApiClient");
        if(_googleApiClient != null) {
            _googleApiClient.disconnect();
        }
    }
    public void onDestroyGoogleApiClient()
    {
        Log.i(TAG, TITLE + " onDestroyGoogleApiClient");
    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, TITLE + " onConnected");
        _fusedLocationProviderApi.requestLocationUpdates(_googleApiClient,  _locationRequest, this);
        _currentLocation = _fusedLocationProviderApi.getLastLocation(_googleApiClient);
        startLocationUpdates();

    }
    protected void stopLocationUpdates() {
        Log.i(TAG, TITLE + " stopLocationUpdates");
        _fusedLocationProviderApi.removeLocationUpdates(_googleApiClient, this);
    }
    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, TITLE + " onLocationChanged " + location.toString());
        _currentLocation = location;
        this._iLocationListener.onGotLocation(_currentLocation);
    }
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, TITLE + " onConnectionSuspended");
        _googleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, TITLE + " onConnectionFailed");
    }


}
