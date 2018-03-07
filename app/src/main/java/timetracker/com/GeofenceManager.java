package timetracker.com;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import timetracker.com.storage.SharedPreferencesManager;

/**
 * Manager for handling geofence tasks - creating geofences and removing them
 */

public class GeofenceManager {
    private static final String TAG = "GeofenceManager";
    private static final String GEOFENCE_REQ_ID = "TimeTRacker";
    private static final float GEOFENCE_RADIUS = 200.0f; // in meters

    private PendingIntent geoFencePendingIntent;

    /**
     * this method initiates geofencing or stops it
     * @param context
     * @param isOn
     */
    public void setGeofencingOn(Context context, boolean isOn){
        if (isOn){
            startGeofencing(context);
        }
        else{
            stopGeofencing(context);
        }
    }

    /**
     * this method sets up geofences
     * @param context
     */
    private void startGeofencing(final Context context) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);

        if (!isLocationPermissionGranted(context)){
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(context), getGeofencePendingIntent(context))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveGeofencingOn(context, true);
                        Log.d(TAG, "Geo fence successfully added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Geo fence could not be added " + e.getMessage());
                        e.printStackTrace();
                        //TODO: show error
                    }
                });
    }

    /**
     * this method removes geofences
     * @param context
     */
    private void stopGeofencing(final Context context) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        geofencingClient.removeGeofences(getGeofencePendingIntent(context))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveGeofencingOn(context, false);
                        }
                    })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        //TODO: show error
                    }
                });
    }

    /**
     * check if location permission was granted
     * @param context
     * @return
     */
    private boolean isLocationPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * save is shared prefs whether the geofencing is on or off
     * @param context
     * @param isOn
     */
    private void saveGeofencingOn(Context context, boolean isOn){
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        sharedPreferencesManager.setValue(SharedPreferencesManager.PREF_GEOFENCE_ON, isOn);
    }

    /**
     * construct here the geofencing request
     * @param context
     * @return
     */
    private GeofencingRequest getGeofencingRequest(Context context) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER); //TODO: change to INITIAL_TRIGGER_DWELL to save battery
        // Add the geofences to be monitored by geofencing service.
        builder.addGeofence(getGeofence(context));
        return builder.build();
    }

    /**
     * construct geofence pending intent
     * @param context
     * @return
     */
    private PendingIntent getGeofencePendingIntent(Context context) {
        // Reuse the PendingIntent if we already have it.
        if (geoFencePendingIntent != null) {
            return geoFencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geoFencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geoFencePendingIntent;
    }

    /**
     * Create a Geofence
     * @param context
     * @return
     */
    private Geofence getGeofence(Context context) {

        SharedPreferencesManager prefManager = new SharedPreferencesManager(context);
        float lat = prefManager.getValueFloat(SharedPreferencesManager.PREF_LAT);
        float lon = prefManager.getValueFloat(SharedPreferencesManager.PREF_LON);

        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( lat, lon, GEOFENCE_RADIUS)
                .setExpirationDuration( Geofence.NEVER_EXPIRE ) //TODO: add button to stop geofencing
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }


}
