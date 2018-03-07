package timetracker.com;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import timetracker.com.storage.SharedPreferencesManager;

/**
 * Main activity that allows the user to add his work address
 */

public class MainActivity extends AppCompatActivity {

    private static final int ADDRESS_ERROR_EMPTY = 1;
    private static final int ADDRESS_ERROR_TOO_SHORT = 2;
    private static final int ADDRESS_ERROR_NOT_FOUND = 3;

    private static final int ADDRESS_OK = 100;
    private static final int ADDRESS_MINIMAL_LENGTH = 10;

    private AddressResultReceiver resultReceiver;
    private boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if user already set the work address, go on to reported hours screen
        if (isAddressValidated()){
            showTimeTrackingScreen();
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        initSaveButton();
        initAddressEditTextView();
        initResultReceiver();
        initValidatingProgress();
    }

    /**
     * open the hours report screen
     */
    private void showTimeTrackingScreen() {
        Intent intent = new Intent(this, HoursReportActivity.class);
        startActivity(intent);
    }

    /**
     *
     * @return true if office address was already validated and saved in shared prefs,
     * false otherwise
     */
    private boolean isAddressValidated() {
        SharedPreferencesManager manager = new SharedPreferencesManager(getApplicationContext());
        String address = manager.getValueString(SharedPreferencesManager.PREF_ADDRESS_SET);
        if ("".equals(address)){
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    private void initSaveButton() {
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSaveBtnPressed();
            }
        });
    }

    /**
     * when save button was pressed:
     * validate input text
     * if text is ok then decode the address
     */
    private void handleSaveBtnPressed() {
        String address = getInputText();
        int addressValidity = checkValidInput(address);
        if (addressValidity == ADDRESS_OK){
            //search for address
            decodeAddress(address);
            showtWaitingForResultUI();
        }
        else{
            showErrorMessage(addressValidity);
        }
        hideKeyboard();
    }

    /**
     * show waiting ui
     */
    private void showtWaitingForResultUI() {
        showValidatingProgress(true);

        Button saveBtn = findViewById(R.id.buttonSave);
        saveBtn.setEnabled(false);

        EditText input = findViewById(R.id.editTextAddress);
        input.setEnabled(false);
    }

    /**
     * start service for decodong address, pass result receiver to the service
     * @param address - the office address to decode
     */
    private void decodeAddress(String address){
        Intent intent = new Intent(this, DecodeAddressIntentService.class);
        intent.putExtra(DecodeAddressIntentService.EXTRA_RECEIVER, resultReceiver);
        intent.putExtra(DecodeAddressIntentService.EXTRA_ADDRESS_STR, address);
        startService(intent);
    }

    /**
     * init result receiver for passing decoding address result
     */
    private void initResultReceiver() {
        resultReceiver = new AddressResultReceiver(new Handler());
    }

    /**
     * init address edit view
     */
    private void initAddressEditTextView() {
        EditText addressEditText = findViewById(R.id.editTextAddress);
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                TextView errorText = findViewById(R.id.textViewError);
                errorText.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initValidatingProgress() {
        showValidatingProgress(false);
    }

    private void showValidatingProgress(boolean show) {
        LinearLayout view = findViewById(R.id.linearLayoutValidating);
        if (show) {
            view.setVisibility(View.VISIBLE);
        }
        else{
            view.setVisibility(View.GONE);
        }
    }

    /**
     * check if user entered valid address text
     * @param address
     * @return true if address is not empty and longer than minimal address length
     */
    private int checkValidInput(String address) {
        if ("".equals(address)){
            return ADDRESS_ERROR_EMPTY;
        }

        if (address.length() < ADDRESS_MINIMAL_LENGTH){
            return ADDRESS_ERROR_TOO_SHORT;
        }

        return ADDRESS_OK;

    }

    private String getInputText(){
        EditText editText = findViewById(R.id.editTextAddress);
        return editText.getText().toString();
    }

    /**
     * set error message for invalid address
     * @param addressValidity
     */
    private void showErrorMessage(int addressValidity) {
        String errorText = "";
        switch (addressValidity){
            case ADDRESS_ERROR_EMPTY:
                errorText = getString(R.string.error_address_empty);
                break;
            case ADDRESS_ERROR_TOO_SHORT:
                errorText = getString(R.string.error_address_too_short);
                break;

            case ADDRESS_ERROR_NOT_FOUND:
                errorText = getString(R.string.error_address_not_found);
                break;
        }

        TextView errorTextView = findViewById(R.id.textViewError);
        errorTextView.setText(errorText);
    }

    private void hideKeyboard() {
        EditText addressEditText = findViewById(R.id.editTextAddress);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addressEditText.getWindowToken(), 0);
    }

    /**
     * this result receiver is passed to address decoding service
     * for sending back to the activity decoding result
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

           if (resultCode == DecodeAddressIntentService.RESULT_OK){
               if (!isPaused) {
                   //move to tracking screen
                   showTimeTrackingScreen();
                   finish();
               }

           }
           else{
               if (!isPaused){
                    showValidatingProgress(false);
                    showErrorMessage(ADDRESS_ERROR_NOT_FOUND);}
           }
           //TODO: handle ui in onResume if activity was paused when result was received.

        }
    }
}
