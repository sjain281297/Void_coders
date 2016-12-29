package com.example.shubham.void_coders;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Collection;

public class SecondActivity extends AppCompatActivity implements BeaconConsumer,RangeNotifier {

    private BeaconManager mBeaconManager;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static String[] mPermissions = { Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String TAG = SecondActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        if (!havePermissions()) {
            Log.i(TAG, "Requesting permissions needed for this app.");
            requestPermissions();
        }

        if(!isBlueEnable()){
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bluetoothIntent);
        }

        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        mBeaconManager.bind(this);



    }



//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//

//    }

    public void onBeaconServiceConnect() {
        Region region = new Region("Python Room", Identifier.parse("0x5dc33487f02e477d4058"),null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.addRangeNotifier(this);
    }



    @Override
    public void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }

    private boolean isBlueEnable() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        return bluetoothAdapter.isEnabled();

    }



    private boolean havePermissions() {
        for(String permission:mPermissions){
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return  false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                mPermissions, PERMISSION_REQUEST_COARSE_LOCATION);
    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        for (final Beacon beacon: collection) {
            // This is a Eddystone-UID frame
            final Identifier namespaceId = beacon.getId1();
            final Identifier instanceId = beacon.getId2();
            Log.i(TAG, "I see a beacon transmitting namespace id: "+namespaceId+
                    " and instance id: "+instanceId+
                    " approximately "+beacon.getDistance()+" meters away.");
            final TextView tv=(TextView)findViewById(R.id.beacon);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText("I see a beacon transmitting namespace id: "+namespaceId+
                            " and instance id: "+instanceId+
                            " approximately "+beacon.getDistance()+" meters away.");
                }
            });


        }
    }
}
