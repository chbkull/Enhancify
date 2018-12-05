package com.fall2018finalproject.cs125.enhancify;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import static com.android.volley.VolleyLog.TAG;

public class APIRequest{




    /** Base 64 encoded client_id:client_client_secret. */
    private static final String COMBINED_KEY = "ZTYwZWFlZTk0MWMyNGQyMTkyMGFlMWZiZjM5MDQ2NWI6OTlhY2U0YTdhYzkyNDY2NGE2NWFkOWNmNDBkNjllYWE=";

    private Context activityContext;

    private RequestQueue requestQueue;


    public String token = null;

    public JSONObject output = null;

    APIRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public String getToken() {
        Log.d(TAG, "Getting token.");
        requestToken(COMBINED_KEY);
        Log.d(TAG, "Token received.");
        return token;
    }

    public JSONObject callApiGET(String access_token, String fullUrl) {
        Log.d(TAG, "Calling API");
        spotifyAPICall(getToken(), fullUrl);
        Log.d(TAG, "API called.");
        return output;
    }

    void requestToken(final String key) {
        Log.d(TAG, "REQUEST TOKEN CALLED");
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
                                token = response.getString("access_token");
                                Log.d(TAG, "TOKEN: " + response.getString("access_token"));
                            } catch (JSONException e) {
                                Log.d(TAG, "oops");
                            }
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
     * @param access_token Token in order to use spotify's servers.
     */
    void spotifyAPICall(final String access_token, String fullUrl) {
        try {
            CustomRequest jsonObjectRequest = new CustomRequest(
                    Request.Method.GET,
                    fullUrl,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                            output = response;
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
                    params.put("Authorization", "Bearer " + access_token);
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

