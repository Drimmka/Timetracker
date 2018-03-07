package timetracker.com;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import timetracker.com.storage.WorkHoursDBHelper;

/**
 * Service for handling geofence events
 *
 */
public class GeofencingJobIntentService extends JobIntentService {

    private static final String TAG = "GeofencingIntentService";
    private static final String CHANNEL_ID = "notification_channel_1";
    private static final int JOB_ID = 123;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofencingJobIntentService.class, JOB_ID, intent);
    }

    /**
     * receive here the geofence event
     * @param intent
     */
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "geofencing error");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Send notification and log the transition details.
            sendNotification(geofenceTransition);

            //save exit/enter entry in db
            addEventToDatabase(geofenceTransition);
            Log.i(TAG, "geofenceTransition: " + geofenceTransition);
        } else {
            // Log the error.
            Log.e(TAG, "invalid transition type");
        }
    }


    private void addEventToDatabase(int geofenceTransition) {
        WorkHoursDBHelper db = new WorkHoursDBHelper(getApplicationContext());

        switch (geofenceTransition){
            case Geofence.GEOFENCE_TRANSITION_ENTER: db.checkIn(System.currentTimeMillis());
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT: db.checkOut(System.currentTimeMillis());
                break;
        }
    }

    /**
     * show exit/enter notification
     * @param transitionType
     */
    private void sendNotification(int transitionType) {
        // Get an instance of the Notification manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
    
            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel);
        }
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    
        // Define the notification settings.
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setColor(Color.RED)
                .setContentIntent(notificationPendingIntent)
                .setContentText(getNotificationText(transitionType));
    
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
    
        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);
    
        // Issue the notification
        notificationManager.notify(0, builder.build());
        
    }

    /**
     *  get notification text according to geofence event type
     * @param transitionType
     * @return
     */
    private String getNotificationText(int transitionType){
        
        String text = "";
        
        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER: text = getString(R.string.entered_office);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT: text = getString(R.string.left_office);
                break;
        }
        
        return text;
    }
}
