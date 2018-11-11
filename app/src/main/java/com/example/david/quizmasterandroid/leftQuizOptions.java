package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.util.ArrayList;

public class leftQuizOptions extends AppCompatActivity {

    LinearLayout L = (LinearLayout) findViewById(R.id.thelayout);
    private int numSwitches = L.getChildCount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_quiz_options);
    }

    //Starta spel och skicka med vilka kategorier som har valts
    public void startTheGame(View view) {
        Intent intent = new Intent(this, StartGame.class);

        ArrayList<Switch> theSwitches = new ArrayList<>();
        theSwitches.add((Switch) findViewById(R.id.switch1));
        theSwitches.add((Switch) findViewById(R.id.switch2));
        theSwitches.add((Switch) findViewById(R.id.switch3));
        theSwitches.add((Switch) findViewById(R.id.switch4));
        theSwitches.add((Switch) findViewById(R.id.switch5));
        theSwitches.add((Switch) findViewById(R.id.switch6));


        for(int i=0; i < numSwitches; i++) {
            if(!theSwitches.get(i).isChecked()) {
                theSwitches.remove(theSwitches.get(i));
            }

            //TODO skicka med de valda kategorierna m.h.a listan theSwitches
        }


    }
}
