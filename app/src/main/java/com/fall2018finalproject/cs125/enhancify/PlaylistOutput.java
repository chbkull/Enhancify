package com.fall2018finalproject.cs125.enhancify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistOutput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_output);

        ArrayList<String> tracklist = getIntent().getStringArrayListExtra("tracks");
        ListView playlistView = findViewById(R.id.tracklist);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringCleaner(tracklist));
        playlistView.setAdapter(adapter);

    }

    private ArrayList<String> stringCleaner(ArrayList<String> rawOutput) {
        ArrayList<String> clean = new ArrayList<>();
        for (String information : rawOutput) {
            String[] split = information.split("\\|");
            clean.add(split[1] + "- " + split[2]);
        }
        return clean;
    }
}
