package com.makartsevaelena.beaconsimulator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

public class MainActivity extends Activity {
    

    // Button for making device Beacon
    private Button b1;
    private BeaconTransmitter beaconTransmitter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_simu);

        //TextView (UUID)
        // Text View for displaying UUID
        TextView uuid = (TextView) findViewById(R.id.uuid_simu);
        //Button
        b1 = (Button) findViewById(R.id.make_beacon);
        // Making of random UUID
        //final String uuid1 = (int) Math.floor(Math.random() * 8 + 1) + "f" + (int) Math.floor(Math.random() * (999999 - 100000) + 100000) + "-" + "cf" + (int) Math.floor(Math.random() * 8 + 1) + "d-4a0f-adf2-f" + (int) Math.floor(Math.random() * 9999 - 1000 + 1000) + "ba9ffa6";
        final String uuid1 = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6";
        // Displaying the random UUID to TextView
        uuid.setText(uuid1);
        // Clicking the Make Beacon Button
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPrerequisites()) {
                    Beacon beacon = new Beacon.Builder()
                            .setId1("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
                            .setId2("1")
                            .setId3("2")
                            .setManufacturer(0x0118)
                            .setTxPower(-59)
                            .setDataFields(Arrays.asList(new Long[]{0l}))
                            .build();

                    beaconTransmitter = new BeaconTransmitter(getApplicationContext(), new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

                    // Start Advertising the above Beacon Object
                    beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
                        @Override
                        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                            super.onStartSuccess(settingsInEffect);
                            Log.d("beacon", "Advertisement start succeeded.");

                        }

                        @Override
                        public void onStartFailure(int errorCode) {
                            super.onStartFailure(errorCode);
                            Log.d("beacon", "Advertisement start failed with code: " + errorCode);
                        }
                    });

                    // Making some animation and changing the text of button
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                    alphaAnimation.setDuration(2000);
                    alphaAnimation.setFillEnabled(true);
                    alphaAnimation.setInterpolator(new BounceInterpolator());
                    b1.startAnimation(alphaAnimation);
                    b1.setText("Beacon Successfully Made");
                }
            }
        });

    }

    @TargetApi(21)
    private boolean checkPrerequisites() {

        if (android.os.Build.VERSION.SDK_INT < 18) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not supported by this device's operating system");
            builder.setMessage("You will not be able to transmit as a Beacon");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }

            });
            builder.show();
            return false;
        }
        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not supported by this device");
            builder.setMessage("You will not be able to transmit as a Beacon");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }

            });
            builder.show();
            return false;
        }
        if (!((BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth not enabled");
            builder.setMessage("Please enable Bluetooth and restart this app.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }

            });
            builder.show();
            return false;

        }

        try {
            // Check to see if the getBluetoothLeAdvertiser is available.  If not, this will throw an exception indicating we are not running Android L
            ((BluetoothManager) this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().getBluetoothLeAdvertiser();
        } catch (Exception e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE advertising unavailable");
            builder.setMessage("Sorry, the operating system on this device does not support Bluetooth LE advertising.  As of July 2014, only the Android L preview OS supports this feature in user-installed apps.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }

            });
            builder.show();
            return false;

        }

        return true;
    }
}
