package com.estimote.examples.demos.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.examples.demos.R;
import com.estimote.examples.demos.adapters.BeaconListAdapter;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Displays list of found beacons sorted by RSSI.
 * Starts new activity with selected beacon if activity was provided.
 *
 * @author wiktor.gworek@estimote.com (Wiktor Gworek)
 */
public class ListBeaconsActivity extends BaseActivity {

    private static final String TAG = ListBeaconsActivity.class.getSimpleName();

    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager;
    private BeaconListAdapter adapter;
    private ListViewDBHelper helper;
    private Cursor cursor;
    private Button Button04;
    private Beacon beacon;
    EditText Macaddress,UUID,Major,Minor,Measuredpower,RSSI;
    Button insert, show;
    RequestQueue requestQueue;
    String insertUrl = "http://140.118.122.148/beacons/insertbeacons.php";
    String showUrl = "http://140.118.122.148/beacons/showBeacons.php";
    TextView result;



    @Override protected int getLayoutResId() {
        return R.layout.main;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        helper = new ListViewDBHelper(getApplicationContext());
        Button04 = (Button) findViewById(R.id.button4);
        insert = (Button) findViewById(R.id.insert);
        show = (Button) findViewById(R.id.showbeacons);
        result = (TextView) findViewById(R.id.textView);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Configure device list.
        cursor = helper.select();
        adapter = new BeaconListAdapter(this);
        final ListView list = (ListView) findViewById(R.id.device_list);

        list.setAdapter(adapter);
        list.setOnItemClickListener(createOnItemClickListener());
        beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);

        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        toolbar.setSubtitle("Found beacons: " + beacons.size());
                        Button04.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
// TODO Auto-generated method stub
                                adapter.replaceWith(beacons);
                                String s = String.valueOf(beacons);
                                String Split = s.substring(1, (s.length() - 1));
                                String[] aArray = Split.split("\\}");
                                //for(int i=0; i<aArray.length; i++)  Log.d("aArray", "aArray: " + aArray[i]);
                                //result.setText(aArray[1].substring(2,(aArray[1].length()))+"}");
                                //切割字串並整理
                                for (int i = 0; i < aArray.length; i++) {
                                    if (i == 0) aArray[i] = aArray[i] + "}";
                                    else if (i < aArray.length)
                                        aArray[i] = (aArray[i].substring(2, (aArray[i].length())) + "}");
                                }
                                for (int i = 0; i < aArray.length; i++)
                                    Log.d("aArray", "resultArray: " + aArray[i]);
                                for (int i = 0; i < aArray.length; i++) {
                                    Pattern p = Pattern.compile("Beacon\\{macAddress=(.*?), *proximityUUID=(.*?), *major=(.*?), *minor=(.*?), *measuredPower=(.*?), *rssi=(.*?)\\}");
                                    Matcher m = p.matcher(aArray[i]);
                                    if (m.matches()) {
                                        Log.d("aArray", "report: " + m.group(0));
                                        Log.d("aArray", "report: " + m.group(1));
                                        Log.d("aArray", "report: " + m.group(2));
                                        Log.d("aArray", "report: " + m.group(3));
                                        Log.d("aArray", "report: " + m.group(4));
                                        Log.d("aArray", "report: " + m.group(5));
                                        Log.d("aArray", "report: " + m.group(6));
                                        // m.group(0) is the entire matched item, not the first group.
                                        // etc...
                                    }
                                }
                            }
                        });

                        insert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adapter.replaceWith(beacons);
                                String s = String.valueOf(beacons);
                                String Split = s.substring(1, (s.length() - 1));
                                String[] aArray = Split.split("\\}");

                                //for(int i=0; i<aArray.length; i++)  Log.d("aArray", "aArray: " + aArray[i]);
                                //result.setText(aArray[1].substring(2,(aArray[1].length()))+"}");
                                //切割字串並整理
                                for(int i = 0; i<aArray.length; i++)
                                {
                                    if(i==0)  aArray[i] = aArray[i]+"}";
                                    else if(i<aArray.length) aArray[i] = (aArray[i].substring(2,(aArray[i].length()))+"}");
                                }
                                for(int i=0; i<aArray.length; i++)  Log.d("aArray", "resultArray: " + aArray[i]);
                                for(int i = 0; i<aArray.length; i++)
                                {
                                    Pattern p = Pattern.compile("Beacon\\{macAddress=(.*?), *proximityUUID=(.*?), *major=(.*?), *minor=(.*?), *measuredPower=(.*?), *rssi=(.*?)\\}");
                                    final Matcher m = p.matcher(aArray[i]);
                                    StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            System.out.println(response.toString());
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }) {

                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> parameters = new HashMap<String, String>();
                                            parameters.put("Macaddress", m.group(1));
                                            parameters.put("UUID", m.group(2));
                                            parameters.put("Major", m.group(3));
                                            parameters.put("Minor", m.group(4));
                                            parameters.put("Measuredpower", m.group(5));
                                            parameters.put("RSSI", m.group(6));

                                            return parameters;
                                        }
                                    };
                                    requestQueue.add(request);
                                    if (m.matches()) {
                                        Log.d("aArray", "report: " + m.group(0));
                                        Log.d("aArray", "report: " + m.group(1));
                                        Log.d("aArray", "report: " + m.group(2));
                                        Log.d("aArray", "report: " + m.group(3));
                                        Log.d("aArray", "report: " + m.group(4));
                                        Log.d("aArray", "report: " + m.group(5));
                                        Log.d("aArray", "report: " + m.group(6));
                                        // m.group(0) is the entire matched item, not the first group.
                                        // etc...
                                    }
                                }



                            }

                        });
                    }
                });
            }
        });

    }

    @Override protected void onDestroy() {
        beaconManager.disconnect();
        helper.close();
        super.onDestroy();
    }

    @Override protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    @Override protected void onStop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        super.onStop();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
                toolbar.setSubtitle("Bluetooth not enabled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {
        toolbar.setSubtitle("Scanning...");
        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(ListBeaconsActivity.this, "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener createOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
                    try {
                        Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
                        Intent intent = new Intent(ListBeaconsActivity.this, clazz);
                        intent.putExtra(EXTRAS_BEACON, adapter.getItem(position));
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "Finding class by name failed", e);
                    }
                }
            }
        };
    }
}
