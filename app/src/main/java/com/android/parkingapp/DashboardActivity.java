package com.android.parkingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.parkingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    public static final String Rejestracjaarray = "Rejestracja";
    public static final String JSON_ARRAY = "result";
    private JSONArray result;
    Spinner spinner;
    String  Rejestracja;
    private ArrayList<String> arrayList;

    ListView listView;
    //TextView wolne_m;
    private SessionHandler session;
    private final Handler handler = new Handler();
    //int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        session = new SessionHandler(getApplicationContext());
        final User user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);
        //wolne_m = (TextView)findViewById(R.id.miejscaText);
        listView = (ListView) findViewById(R.id.listView);
        downloadJSON("http://filipsmolinski037.eu/andr/wolne_miejsca.php");
        welcomeText.setText("Witaj "+user.getFullName());

        spinner= (Spinner) findViewById(R.id.spinner);
        arrayList = new ArrayList<String>();
        getdata();
        doTheAutoRefresh();



        Button logoutBtn = findViewById(R.id.btnLogout);

        Button szlabanBtn = findViewById(R.id.szlabanButton);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        });

        szlabanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sz_username = user.getFullName();
                Spinner spinner = (Spinner)findViewById(R.id.spinner);
                String rejestracja = spinner.getSelectedItem().toString();
                listView = (ListView) findViewById(R.id.listView);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                                builder.setMessage("Szlaban się otwiera!")
                                        .setNegativeButton("Ok", null)
                                        .create()
                                        .show();

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                                builder.setMessage("Coś sie popsuło :<")
                                        .setNegativeButton("Jeszcze raz", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                UpdateSzlaban updateSzlaban = new UpdateSzlaban(sz_username, rejestracja, responseListener);
                RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);
                queue.add(updateSzlaban);
                updateSzlaban.setTag(this);
                queue.cancelAll(updateSzlaban);
                queue.getCache().clear();

            }

        });



    }

    private void getdata() {
        final User user = session.getUserDetails();
        StringRequest stringRequest = new StringRequest("http://filipsmolinski037.eu/andr/rejestracje.php?username="+user.getFullName(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(JSON_ARRAY);
                            empdetails(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void empdetails(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                arrayList.add(json.getString(Rejestracjaarray));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // arrayList.add(0,"Select Employee");
        spinner.setAdapter(new ArrayAdapter<String>(DashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList));
    }

    private void downloadJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] stocks = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            stocks[i] = obj.getString("wolne_miejsca") ;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stocks);
        listView.setAdapter(arrayAdapter);
    }
    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadJSON("http://filipsmolinski037.eu/andr/wolne_miejsca.php");
                doTheAutoRefresh();
            }
        }, 2000);
    }

}
