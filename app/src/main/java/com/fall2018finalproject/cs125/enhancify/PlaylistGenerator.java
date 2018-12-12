package com.fall2018finalproject.cs125.enhancify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistGenerator extends AppCompatActivity {

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "PlaylistGenerator";

    /** Base 64 encoded client_id:client_client_secret. */
    private static final String COMBINED_KEY = "ZTYwZWFlZTk0MWMyNGQyMTkyMGFlMWZiZjM5MDQ2NWI6OTlhY2U0YTdhYzkyNDY2NGE2NWFkOWNmNDBkNjllYWE=";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    private static String[] validGenres = new String[]{"acoustic", "afrobeat", "alt-rock", "alternative",
    "ambient", "anime", "black-metal", "bluegrass", "blues", "bossanova", "brazil", "breakbeat", "british",
    "cantopop", "chicago-house", "children", "chill", "classical", "club", "comedy", "country", "dance",
    "dancehall", "death-metal", "deep-house", "detroit-techno", "disco", "disney", "drum-and-bass",
    "dub", "dubstep", "edm", "electro", "electronic", "emo", "folk", "forro", "french", "funk", "garage",
    "german", "gospel", "goth", "grindcore", "groove", "grunge", "guitar", "happy", "hard-rock", "hardcore",
    "hardstyle", "heavy-metal", "hip-hop", "holidays", "honky-tonk", "house", "idm", "indian", "indie",
    "indie-pop", "industrial", "iranian", "j-dance", "j-idol", "j-pop", "j-rock", "jazz", "k-pop",
    "kids", "latin", "latino", "malay", "mandopop", "metal", "metal-misc", "metalcore", "minimal-techno",
    "movies", "mpb", "new-age", "new-release", "opera", "pagode", "party", "philippines-opm", "piano",
    "pop", "pop-film", "post-dubstep", "power-pop", "progressive-house", "psych-rock", "punk", "punk-rock",
    "r-n-b", "rainy-day", "reggae", "reggaeton", "road-trip", "rock", "rock-n-roll", "rockabilly", "romance",
    "sad", "salsa", "samba", "sertanejo", "show-tunes", "singer-songwriter", "ska", "sleep", "songwriter",
    "soul", "soundtracks", "spanish", "study", "summer", "swedish", "synth-pop", "tango", "techno", "trance",
    "trip-hop", "turkish", "work-out", "world-music"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_playlist_generator);

        configureBackButton();
        configureRandomButton();

        Spinner dropdown = findViewById(R.id.genreList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, validGenres);
        dropdown.setAdapter(adapter);

        final Button generatePlaylist = findViewById(R.id.generatePlaylist);
        generatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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

    private void configureRandomButton() {
        Button randomButton = (Button) findViewById(R.id.randomButton);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomize();
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
                            try {
                                String accessKey = response.getString("access_token");
                                spotifyAPICall(accessKey);
                            } catch (JSONException e) {
                            }
                            //output.setText(response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Basic " + COMBINED_KEY);
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
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
                    "https://api.spotify.com/v1/recommendations" + queryBuilder(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
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

                                Intent launchResult = new Intent(PlaylistGenerator.this, PlaylistOutput.class);
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

    private String queryBuilder() {
        String complete = "?limit=10&market=US&seed_genres=";
        Spinner genreInput = findViewById(R.id.genreList);
        complete = complete + genreInput.getSelectedItem().toString() + "&";
        complete = complete + "target_tempo" + Integer.toString((((SeekBar) findViewById(R.id.tempoBar)).getProgress()) + 30) + "&";
        complete = complete + "target_valence" + Double.toString((double) (((SeekBar) findViewById(R.id.valanceBar)).getProgress()) / 100.0) + "&";
        complete = complete + "target_popularity" + Integer.toString(((SeekBar) findViewById(R.id.popularityBar)).getProgress()) + "&";
        complete = complete + "target_danceability" + Double.toString((double) (((SeekBar) findViewById(R.id.danceabilityBar)).getProgress()) / 100.0) + "&";
        complete = complete + "target_energy" + Double.toString((double) (((SeekBar) findViewById(R.id.energyBar)).getProgress()) / 100.0);

        return complete;
    }

    private void randomize() {
        Log.d(TAG, "LENGTH: " + Integer.toString(validGenres.length));
        ((Spinner) findViewById(R.id.genreList)).setSelection((int) (Math.random() * validGenres.length));
        ((SeekBar) findViewById(R.id.tempoBar)).setProgress((int) (Math.random() * 151));
        ((SeekBar) findViewById(R.id.valanceBar)).setProgress((int) (Math.random() * 101));
        ((SeekBar) findViewById(R.id.popularityBar)).setProgress((int) (Math.random() * 101));
        ((SeekBar) findViewById(R.id.danceabilityBar)).setProgress((int) (Math.random() * 101));
        ((SeekBar) findViewById(R.id.energyBar)).setProgress((int) (Math.random() * 101));
    }
}