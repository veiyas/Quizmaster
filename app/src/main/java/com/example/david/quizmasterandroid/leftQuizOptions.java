package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class leftQuizOptions extends AppCompatActivity {

    ArrayList<Switch> theSwitches = new ArrayList<>();
    TextView errorText;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_quiz_options);

        //Sätt errorText och avaktivera start knappen från början så att det inte går att starta direkt
        errorText = (TextView) findViewById(R.id.errorText);
        errorText.setText("Du måste välja fyra kategorier!");
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setClickable(false); startButton.setText("Inte redo!");
        startButton.setEnabled(false); startButton.setTextColor(Color.GRAY);

        //Sätt en actionlistener på alla switchar
        theSwitches.add((Switch) findViewById(R.id.switch1));
        theSwitches.add((Switch) findViewById(R.id.switch2));
        theSwitches.add((Switch) findViewById(R.id.switch3));
        theSwitches.add((Switch) findViewById(R.id.switch4));
        theSwitches.add((Switch) findViewById(R.id.switch5));
        theSwitches.add((Switch) findViewById(R.id.switch6));

        for(int i=0; i<theSwitches.size(); i++) {
            theSwitches.get(i).setOnClickListener(readyCheck);
        }
    }

    View.OnClickListener readyCheck = new View.OnClickListener() {
        public void onClick(View v) {
            int switchesPressed = 0;

            for(int i=0; i<theSwitches.size(); i++) {
                if(theSwitches.get(i).isChecked()) {
                    switchesPressed++;
                }
            }
            if(switchesPressed == 4) {
                errorText.setText("Redo!");

                startButton.setEnabled(true); startButton.setTextColor(Color.WHITE);
                startButton.setClickable(true); startButton.setText("Start!");
            }
            else {
                errorText.setText("Du måste välja fyra kategorier!");

                startButton.setEnabled(false); startButton.setTextColor(Color.GRAY);
                startButton.setClickable(false); startButton.setText("Inte redo!");
            }

        }
    };

    //Start game
    public void startTheGame(View view) {
        Intent startIntent = new Intent(leftQuizOptions.this, StartGameActivity.class);

        sharedMethods start = new sharedMethods();

        startIntent = start.putExtras(leftQuizOptions.this, startIntent);

        startActivity(startIntent);
    }
}
