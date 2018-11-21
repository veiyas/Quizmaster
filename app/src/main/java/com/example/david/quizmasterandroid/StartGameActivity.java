package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class StartGameActivity extends AppCompatActivity {

    //JSON grejer
    InputStream is;
    JSONObject reader;

    public JSONArray cat1json;
    public JSONArray cat2json;
    public JSONArray cat3json;
    public JSONArray cat4json;

    Intent thisIntent = getIntent();
    ConstraintLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        Bundle extras = getIntent().getExtras();
        String cat1 = extras.getString("cat1");
        String cat2 = extras.getString("cat2");
        String cat3 = extras.getString("cat3");
        String cat4 = extras.getString("cat4");

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            cat1json = obj.getJSONArray(cat1);
            cat2json = obj.getJSONArray(cat2);
            cat3json = obj.getJSONArray(cat3);
            cat4json = obj.getJSONArray(cat4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView catName = (TextView) findViewById(R.id.catName); catName.setText(cat1);

        main = (ConstraintLayout) findViewById((R.id.main));
        main.setOnClickListener(continueCheck);
    }

    View.OnClickListener continueCheck = new View.OnClickListener() {
        public void onClick(View v) {
            //Gör skärmen otryckbar och ta bort element
            main.setClickable(false);   main.removeAllViews();

            putFirstQuestion();
        }
    };

    void putFirstQuestion() {
        //Uppdatera layouten med första frågan osv
        try {
            //Skapa TextView objekt med frågan
            TextView questionText = new TextView(main.getContext());
            questionText.setText(cat1json.getJSONObject(0).getString("question"));

            //Egenskaper
            questionText.setTextSize(28); questionText.setPadding(0,150,0,200);
            questionText.setGravity(Gravity.CENTER);

            main.addView(questionText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}
