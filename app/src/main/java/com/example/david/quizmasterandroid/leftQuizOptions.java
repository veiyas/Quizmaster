package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class leftQuizOptions extends AppCompatActivity {

    public final int maxCatsChosen = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_quiz_options);
    }

    //Starta spel och skicka med vilka kategorier som har valts
    public void startTheGame(View view) {
        Intent intent = new Intent(leftQuizOptions.this, StartGameActivity.class);

        ArrayList<Switch> theSwitches = new ArrayList<>();
        theSwitches.add((Switch) findViewById(R.id.switch1));
        theSwitches.add((Switch) findViewById(R.id.switch2));
        theSwitches.add((Switch) findViewById(R.id.switch3));
        theSwitches.add((Switch) findViewById(R.id.switch4));
        theSwitches.add((Switch) findViewById(R.id.switch5));
        theSwitches.add((Switch) findViewById(R.id.switch6));

        ArrayList<Switch> falseSwitches = new ArrayList<>();

        for(int i=0; i < theSwitches.size(); i++) {
            if(!theSwitches.get(i).isChecked()) {
                falseSwitches.add(theSwitches.get(i));
            }
        }

        for(int i=0; i < falseSwitches.size(); i++) {
            theSwitches.remove(falseSwitches.get(i));
        }

        for(int i=0; i < maxCatsChosen; i++) {
            String catNumber = "cat" + Integer.toString(i+1);
            intent.putExtra(catNumber, theSwitches.get(i).getText());
        }

        startActivity(intent);
    }
}
