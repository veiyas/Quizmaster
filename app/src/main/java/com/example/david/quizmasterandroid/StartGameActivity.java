package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StartGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        Bundle extras = getIntent().getExtras();
        String cat1 = extras.getString("cat1");
        String cat2 = extras.getString("cat2");
        String cat3 = extras.getString("cat3");
        String cat4 = extras.getString("cat4");

        TextView t1 = (TextView) findViewById(R.id.textView1); t1.setText(cat1);
        TextView t2 = (TextView) findViewById(R.id.textView2); t2.setText(cat2);
        TextView t3 = (TextView) findViewById(R.id.textView3); t3.setText(cat3);
        TextView t4 = (TextView) findViewById(R.id.textView4); t4.setText(cat4);
    }
}
