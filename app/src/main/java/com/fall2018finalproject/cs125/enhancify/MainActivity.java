package com.fall2018finalproject.cs125.enhancify;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



/**
 * Main screen for our API testing app.
 */
public final class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /** Base 64 encoded client_id:client_client_secret. */
    private static final String COMBINED_KEY = "ZTYwZWFlZTk0MWMyNGQyMTkyMGFlMWZiZjM5MDQ2NWI6OTlhY2U0YTdhYzkyNDY2NGE2NWFkOWNmNDBkNjllYWE=";
    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up a queue for our Volley requests
        requestQueue = Volley.newRequestQueue(this);

        // Load the main layout for our activity
        setContentView(R.layout.activity_main);

        // Attach the handler to our UI button
        final Button tokenAPICall = findViewById(R.id.startAPICall);
        tokenAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");

                //dummy();
                tokenAPICall();
            }
        });

        // Make sure that our progress bar isn't spinning and style it a bit
        //ProgressBar progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);

        configureNextButton();
    }

    private void configureNextButton() {
        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PlaylistGenerator.class));
            }
        });
    }

    /**
     * Make an API call.
     */


    void dummy() {
        APIRequest apiRequest = new APIRequest(this);
        requestQueue.start();
        JSONObject temp = apiRequest.callApiGET(
                "blah", "https://api.spotify.com/v1/browse/new-releases");
        TextView output = findViewById(R.id.jsonResult);
        output.setText(temp.toString());
    }




    void tokenAPICall() {

        try {
            Map<String, String> map = new HashMap<>();
            map.put("grant_type", "client_credentials");
            CustomRequest jsonObjectRequest = new CustomRequest(
                    Request.Method.POST,
                    "https://accounts.spotify.com/api/token",
                    map,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                            TextView output = findViewById(R.id.jsonResult);
                            try {
                                String accessKey = response.getString("access_token");
                                Log.d(TAG, "access_token: " + accessKey);
                                spotifyAPICall(accessKey);
                            } catch (JSONException e) {
                                Log.d(TAG, "oops");
                            }
                            output.setText(response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Basic " + COMBINED_KEY);
                    Log.d(TAG, params.toString());
                    return params;
                }
            };
            Log.d(TAG, "JsonObjectRequest:");
            Log.d(TAG, jsonObjectRequest.toString());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.d(TAG, "Authorization failed");
            e.printStackTrace();
        }

    }

    /**
     * Actual call to Spotify's servers.
     * @param authorizationKey Key in order to use spotify's servers.
     */
    void spotifyAPICall(final String authorizationKey) {
        try {
            CustomRequest jsonObjectRequest = new CustomRequest(
                    Request.Method.GET,
                    "https://api.spotify.com/v1/browse/new-releases",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                            TextView output = findViewById(R.id.jsonResult);
                            output.setText(response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + authorizationKey);
                    Log.d(TAG, params.toString());
                    return params;
                }
            };
            Log.d(TAG, "JsonObjectRequest:");
            Log.d(TAG, jsonObjectRequest.toString());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.d(TAG, "Authorization failed");
            e.printStackTrace();
        }
    }
}
