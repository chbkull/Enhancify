package com.fall2018finalproject.cs125.enhancify;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
        configureNextButton();
        configurePlaylistGeneratorSongButton();
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

    private void configurePlaylistGeneratorSongButton() {
        Button nextButton = (Button) findViewById(R.id.playlistGeneratorSongButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PlaylistGeneratorSong.class));
            }
        });
    }
}
