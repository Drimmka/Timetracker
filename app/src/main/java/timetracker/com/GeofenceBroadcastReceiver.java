package timetracker.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receiver for geofencing events
 * the receiver enqueues the event intent in GeofencingJobIntentService queue
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    /**
     * Receives incoming intents.
     *
     * @param context the application context.
     * @param intent  sent by Location Services. This Intent is provided to Location
     *                Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        GeofencingJobIntentService.enqueueWork(context, intent);
    }
}
