package com.rain.imageloaderdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.photo_wall);
        PhotoAdapter adapter = new PhotoAdapter(this,Images.imageThumbUrls,mListView);
        mListView.setAdapter(adapter);
    }
}
