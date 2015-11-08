package sl.com.lib.gpsultility;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by shenlong on 8/26/2015.
 */





public class GPSUltility implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {


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
        return _currentLocation;
    }

    protected synchronized void buildGoogleApiClient() {
        createLocationRequest();
        _googleApiClient = new GoogleApiClient.Builder(this._context, this, this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        _googleApiClient.connect();

    }
    protected void createLocationRequest() {
        _locationRequest = new LocationRequest();
        _locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        _locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(_googleApiClient, _locationRequest, this);
    }

    public void onStartGoogleApiClient() {
        _googleApiClient.connect();
    }
    public void onResumeGoogleApiClient() {
        if (_googleApiClient.isConnected())
        {
            startLocationUpdates();
        }
    }
    public void onPauseGoogleApiClient() {
        if (_googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }


    public void onStopGoogleApiClient() {
        _googleApiClient.disconnect();
    }
    public void onDestroyGoogleApiClient()
    {

    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        _fusedLocationProviderApi.requestLocationUpdates(_googleApiClient,  _locationRequest, this);
        _currentLocation = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        startLocationUpdates();

    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(_googleApiClient, this);
    }
    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        _currentLocation = location;
        this._iLocationListener.onGotLocation(_currentLocation);
    }
    @Override
    public void onConnectionSuspended(int cause) {
        _googleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }


}
