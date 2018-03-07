package timetracker.com;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import timetracker.com.storage.SharedPreferencesManager;

/**
 * Service for converting textual address to lat/lon coordinates
 */

public class DecodeAddressIntentService extends IntentService {
    public static final String EXTRA_ADDRESS_STR = "EXTRA_ADDRESS_STR";
    public static final String EXTRA_RECEIVER = "EXTRA_RECEIVER";

    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 0;

    private static final int MAX_ADDRESSES = 10;
    private static final String TAG = "DecodeAddressIntentService";

    public DecodeAddressIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String addressString = intent.getStringExtra(EXTRA_ADDRESS_STR);
        ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);

        // Check if receiver was properly registered.
        if (resultReceiver == null) {
            return;
        }

        if (addressString == null || "".equals(addressString)) {
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(addressString, MAX_ADDRESSES);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size() == 0){
            sendResultToReceiver(RESULT_FAIL, resultReceiver);
        }
        else{
            sendResultToReceiver(RESULT_OK, resultReceiver);
            saveAddressToSharedPrefs(addressString, addresses);
            //shtand 12: lon: 34.7775887 lat: 32.0784346
        }

    }

    /**
     * when decoding is done, save the string address, lattitude, longitude in shared prefs
     * @param addressString - string entered by user
     * @param addresses - decoded address string
     */
    private void saveAddressToSharedPrefs(String addressString, List<Address> addresses) {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        sharedPreferencesManager.setValue(SharedPreferencesManager.PREF_ADDRESS_SET, addressString);
        sharedPreferencesManager.setValue(SharedPreferencesManager.PREF_LAT, (float)addresses.get(0).getLatitude());
        sharedPreferencesManager.setValue(SharedPreferencesManager.PREF_LON, (float) addresses.get(0).getLongitude());
    }

    /**
     * send decoding result code to requesting activity
     * @param resultCode
     * @param resultReceiver
     */
    private void sendResultToReceiver(int resultCode, ResultReceiver resultReceiver) {
        resultReceiver.send(resultCode, null);
    }
}