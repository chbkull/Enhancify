package com.fall2018finalproject.cs125.enhancify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlaylistGeneratorSongs extends AppCompatActivity {

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "PlaylistGenerator";

    /** Base 64 encoded client_id:client_client_secret. */
    private static final String COMBINED_KEY = "ZTYwZWFlZTk0MWMyNGQyMTkyMGFlMWZiZjM5MDQ2NWI6OTlhY2U0YTdhYzkyNDY2NGE2NWFkOWNmNDBkNjllYWE=";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_playlist_generator_songs);

        configureBackButton();

        final Button generatePlaylist = findViewById(R.id.generatePlaylist);
        generatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "generatePlaylist button hit.");

                //dummy();
                tokenAPICall();
            }
        });
    }

    private void configureBackButton(){
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                                spotifyAPICall(accessKey);
                            } catch (JSONException e) {
                                Log.d(TAG, "oops");
                            }
                            //output.setText(response.toString());
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
        Log.d(TAG, queryBuilder());
        try {
            CustomRequest jsonObjectRequest = new CustomRequest(
                    Request.Method.GET,
                    "https://api.spotify.com/v1/recommendations" + queryBuilder(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {
                                JSONArray trackArray = response.getJSONArray("tracks");
                                for (int trackIndex = 0; trackIndex < trackArray.length(); trackIndex++) {
                                    //Log.d("");
                                }
                                //Log.d(TAG, "tracks: " + tracks);
                            } catch (JSONException e) {
                                Log.d(TAG, "something failed");
                            }
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

    private String queryBuilder() {
        String complete = "?limit=10&market=US&seed_genres=";
        EditText genreInput = findViewById(R.id.genreInput);
        complete = complete + genreInput.getText().toString() + "&";
        complete = complete + "target_tempo" + Integer.toString((((SeekBar) findViewById(R.id.tempoBar)).getProgress()) + 30) + "&";
        complete = complete + "target_valence" + Double.toString((double) (((SeekBar) findViewById(R.id.valanceBar)).getProgress()) / 100.0) + "&";
        complete = complete + "target_popularity" + Integer.toString(((SeekBar) findViewById(R.id.popularityBar)).getProgress()) + "&";
        complete = complete + "target_danceability" + Double.toString((double) (((SeekBar) findViewById(R.id.danceabilityBar)).getProgress()) / 100.0) + "&";
        complete = complete + "target_energy" + Double.toString((double) (((SeekBar) findViewById(R.id.energyBar)).getProgress()) / 100.0);

        return complete;
    }
}
