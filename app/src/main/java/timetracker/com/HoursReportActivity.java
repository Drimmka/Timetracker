package timetracker.com;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import timetracker.com.storage.SharedPreferencesManager;
import timetracker.com.storage.WorkHoursDBHelper;

public class HoursReportActivity extends AppCompatActivity implements OnLoadFromDBCompletedListener {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 10;

    private RecyclerView recyclerView;
    private HoursReportAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private GeofenceManager geofenceManager;

    /**
     * this broadcast receiver is for refreshing the ui
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hours_report);
        geofenceManager = new GeofenceManager();
        initRecyclerView();
        initAddress();
        initGeofencingSwitch();
        Log.v("DCDFDFFD", System.currentTimeMillis() + " : " + StringFormatUtils.formatEntryTime(System.currentTimeMillis(), StringFormatUtils.FORMAT_TIME_ENTRY));
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refresh records when resuming the activity
        //TODO: also add listener to refresh the screen on the fly
        loadTimeRecords();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewHoursReport);
        layoutManager = new LinearLayoutManager(HoursReportActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HoursReportAdapter(new ArrayList<WorkEntry>(), HoursReportActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private void loadTimeRecords() {
        WorkHoursDBHelper db = new WorkHoursDBHelper(getApplicationContext());
        db.getAllEntries(this);
    }

    private void initGeofencingSwitch() {
        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(getApplicationContext());
        boolean geofencingOn = preferencesManager.getValueBoolean(SharedPreferencesManager.PREF_GEOFENCE_ON);
        Switch switchView = findViewById(R.id.switchLocationMonitoring);
        switchView.setChecked(geofencingOn);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGeofencingState(isChecked);
                setSwitchText(isChecked);
            }
        });
        setSwitchText(geofencingOn);
    }

    private void setSwitchText(boolean geofencingOn) {
        Switch switchView = findViewById(R.id.switchLocationMonitoring);
        switchView.setText(R.string.monitor_location_off);
        if (geofencingOn){
            switchView.setText(R.string.monitor_location);
        }
    }


    private void setGeofencingState(boolean isChecked) {
        geofenceManager.setGeofencingOn(getApplicationContext(), isChecked);
    }

    private void initAddress() {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        String address = sharedPreferencesManager.getValueString(SharedPreferencesManager.PREF_ADDRESS_SET);
        TextView textView = findViewById(R.id.textViewAddress);
        textView.setText(getString(R.string.address) + " " + address);
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {

        ActivityCompat.requestPermissions(HoursReportActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                setGeofencingState(true);
            } else {
                showSnackbar(getString(R.string.location_permission_denied));
            }
        }
    }

    private void showSnackbar(String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoadCompleted(final ArrayList<WorkEntry> hoursList) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (hoursList != null && !hoursList.isEmpty()) {
                    adapter.setWorkHoursList(hoursList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
