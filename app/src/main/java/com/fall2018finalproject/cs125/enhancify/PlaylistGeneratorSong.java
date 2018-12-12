package com.fall2018finalproject.cs125.enhancify;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistGeneratorSong extends AppCompatActivity {

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "PlaylistGenerator";

    /** Base 64 encoded client_id:client_client_secret. */
    private static final String COMBINED_KEY = "ZTYwZWFlZTk0MWMyNGQyMTkyMGFlMWZiZjM5MDQ2NWI6OTlhY2U0YTdhYzkyNDY2NGE2NWFkOWNmNDBkNjllYWE=";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /** send access token to the song to id function **/
    public static String accessToken;

    /** used in changing song to id */
    public static String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_playlist_generator_song);
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
                            try {
                                String accessKey = response.getString("access_token");
                                accessToken = accessKey;
                                queryBuilder(accessKey);
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
    void spotifyAPICall(final String authorizationKey, String query) {
        //Log.d(TAG, queryBuilder());
        try {
            CustomRequest jsonObjectRequest = new CustomRequest(
                    Request.Method.GET,
                    "https://api.spotify.com/v1/recommendations" + query,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {
                                ArrayList<String> trackList = new ArrayList<>();
                                JSONArray tracks = response.getJSONArray("tracks");
                                for (int trackIndex = 0; trackIndex < tracks.length(); trackIndex++) {
                                    JSONObject currentTrack = tracks.getJSONObject(trackIndex);
                                    String information = currentTrack.getString("id") + "|";
                                    information += tracks.getJSONObject(trackIndex).getString("name") + "|";
                                    JSONArray artists = currentTrack.getJSONArray("artists");
                                    for (int artistIndex = 0; artistIndex < artists.length(); artistIndex++) {
                                        JSONObject currentArtist = artists.getJSONObject(artistIndex);
                                        information += currentArtist.getString("name") + ", ";
                                    }
                                    //trim final comma and space
                                    information = information.substring(0, information.length() - 2);
                                    Log.d(TAG, information);
                                    trackList.add(information);
                                }

                                Intent launchResult = new Intent(PlaylistGeneratorSong.this, PlaylistOutput.class);
                                launchResult.putStringArrayListExtra("tracks", trackList);
                                startActivity(launchResult);
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

    private void queryBuilder(String authorizationKey) {
        EditText listLimit = findViewById(R.id.listLength);
        String limit = listLimit.getText().toString();
        String complete = "?limit=" + limit + "&market=US&seed_artists=";
        //do artists list
        EditText artistInput = findViewById(R.id.artistList);

        List<String> artistArr = stringToList(artistInput.getText().toString());

        for (int i = 0; i < artistArr.size(); i++) {
            String temp = artistArr.get(i);
            songChangeAPICall(accessToken, "artist", changeCommas(temp));
            complete = complete + currentId;
        }
        complete = complete + "&seed_tracks=";
        Log.d(TAG, "Complete:" + complete);
        //do seed tracks
        EditText trackInput = findViewById(R.id.songList);

        List<String> trackArr = stringToList(trackInput.getText().toString());
        for (int i = 0; i < trackArr.size(); i++) {
            String temp = trackArr.get(i);
            songChangeAPICall(accessToken, "track", changeCommas(temp));
            complete = complete + currentId;
        }
        Log.d(TAG, "Complete query builder: " + complete);
        spotifyAPICall(authorizationKey, complete);
    }
    
    
    /**
     * return id for artist or song
     * @param authorizationKey
     * @param type "track" -> track "artist" -> artist
     * @param name name of artist or track to convert
     */
    void songChangeAPICall(final String authorizationKey, final String type, final String name) {
        //Log.d(TAG, queryBuilder());
        try {
            CustomRequest jsonObjectRequest = new CustomRequest(
                    Request.Method.GET,
                    "https://api.spotify.com/v1/search?q=" + queryBuilderSong(name, type),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                JSONObject output;
                                if (type.equals("track")) {
                                    output = response.getJSONObject("tracks");
                                } else {
                                    output = response.getJSONObject("artists");
                                }
                                JSONArray outputArray = output.getJSONArray("items");
                                currentId = outputArray.getJSONObject(0).getString("id");
                                Log.d(TAG, "ID of " + name + ": " + currentId);
                            } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                                Log.d(TAG, "something failed");
                                Log.d(TAG, "Response: " + response.toString());
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

    
    private static List<String> stringToList(String input) {
        List<String> items = Arrays.asList(input.split(","));
        return items;
    }
    private static String changeCommas(String input) {
        //change string, commas -> %2C%20, spaces -> %20
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                output += "%20";
            } else if (input.charAt(i) == ','){
                output += "%2C";
            } else {
                output += input.charAt(i);
            }
        }
        return output;
    }
    private String queryBuilderSong(String name, String type) {
        String complete = name;
        if (type.equals("track")) {
            complete = complete + "&type=track";
        } else {
            complete = complete + "&type=artist";
        }
        Log.d(TAG, "QueryBuilderSong: " + complete);
        return complete;
    }
}
