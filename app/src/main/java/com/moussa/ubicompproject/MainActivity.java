package com.moussa.ubicompproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    TextView mBeaconName, mBeaconRssi;

    int RSSI_THRESHOLD = -90;

    GimbalEventReceiver gimbalEventReceiver;
    PlaceEventListener placeEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String permissions[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.INTERNET};
        requestPermissions(permissions, 0);

        mBeaconName = findViewById(R.id.beacon_name);
        mBeaconRssi = findViewById(R.id.beacon_rssi);

        Gimbal.setApiKey(getApplication(), BuildConfig.API_KEY_GIMBAL);
        setupGimbalPlaceManager();
        Gimbal.start();

//        try{
//            BluetoothDevice bldevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:16:53:4E:E0:3F");
//            CommInterface ci = new BluetoothComm(bldevice);
//
//            Brick brick = new Brick(ci);
//
//            ci.connect();
//
//            Log.d("MainActivityy","Running motor");
//            brick.getMotor().turnAtSpeed((byte)(Motor.PORT_ALL), 10);
//
//            ci.disconnect();
//        } catch(Exception ex){
//            Log.w("MainActivityy", "Error", ex);
//        }


//        Brick brick = new Brick(new BluetoothComm("0016534EE03F"));
//

//        brick.getSpeaker().playTone(100, 600, 1000);

    }

    private void setupGimbalPlaceManager() {
        placeEventListener = new PlaceEventListener(){
            @Override
            public void onVisitStart(Visit visit) {

                Log.d(TAG, "Visit Start: ".concat(visit.getPlace().getName()));
                mBeaconName.setText("You're at: ".concat(visit.getPlace().getName()));

                Intent intent = new Intent();
                intent.setAction("GIMBAL_EVENT_ACTION");
                sendBroadcast(intent);
            }

            @Override
            public void onVisitEnd(Visit visit) {

                Log.d(TAG, "Visit End: ".concat(visit.getPlace().getName()));

                mBeaconName.setText("You're are leaving: ".concat(visit.getPlace().getName()));


                Intent intent = new Intent();
                intent.setAction("GIMBAL_EVENT_ACTION");
                sendBroadcast(intent);
            }

            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting, List<Visit> list) {
                Log.d(TAG, "Beacon sighting: ".concat(beaconSighting.getBeacon().getName()).concat(" RSSI: ").concat(beaconSighting.getRSSI().toString()));

                mBeaconRssi.setText(beaconSighting.getRSSI().toString());

                if(beaconSighting.getRSSI() > RSSI_THRESHOLD){
                    mBeaconName.setText("You're at: ".concat(beaconSighting.getBeacon().getName()));
                } else{
                    mBeaconName.setText("You're leaving: ".concat(beaconSighting.getBeacon().getName()));

                }

            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onStart() {
        super.onStart();
        gimbalEventReceiver = new GimbalEventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("GIMBAL_EVENT_ACTION");
        registerReceiver(gimbalEventReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(gimbalEventReceiver);
    }

    class GimbalEventReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() != null){
                if(intent.getAction().compareTo("GIMBAL_EVENT_ACTION") == 0){
                    Log.d(TAG, "An event!!");
                }
            }
        }
    }
}
