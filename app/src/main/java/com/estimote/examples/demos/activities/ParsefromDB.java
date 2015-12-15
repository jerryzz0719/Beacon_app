package com.estimote.examples.demos.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.examples.demos.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TK on 2015/11/14.
 */
public class ParsefromDB extends Activity {
    private String jsonResult;
    private String url = "http://140.118.122.148/beacons/showbeacons2.php";
    private String delurl = "http://140.118.122.148/beacons/deleteTable.php";
    private ListView listView;
    private RequestQueue mQueue;
    private Button delButton;
    private StringRequest getRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parsefromdb);
        listView = (ListView) findViewById(R.id.listView);
        accessWebService();
        delButton = (Button) findViewById(R.id.deleteDB);
        mQueue = Volley.newRequestQueue(this);
        getRequest = new StringRequest(delurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        delButton.setText(s);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        delButton.setText(volleyError.getMessage());
                    }
                });
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                mQueue.add(getRequest);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            }

            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }

            catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }

    // build hash set for list view
    public void ListDrwaer() {
        List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("Beacons");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String Macaddress = jsonChildNode.optString("Macaddress");
                String UUID = jsonChildNode.optString("UUID");
                String Major = jsonChildNode.optString("Major");
                String Minor = jsonChildNode.optString("Minor");
                String Measuredpower = jsonChildNode.optString("Measuredpower");
                String RSSI = jsonChildNode.optString("RSSI");
                String outPut = "MacAddress: " + Macaddress + "\n" + "UUID: " + UUID + "\n" + "Major: " + Major + "\n" + "Minor: " + Minor + "\n" + "Measuredpower: " + Measuredpower + "\n" + "RSSI: " + RSSI;
                employeeList.add(createEmployee("employees", outPut));
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, employeeList,
                android.R.layout.simple_list_item_1,
                new String[] { "employees" }, new int[] { android.R.id.text1 });
        listView.setAdapter(simpleAdapter);
    }

    private HashMap<String, String> createEmployee(String name, String number) {
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put(name, number);
        return employeeNameNo;
    }
}
