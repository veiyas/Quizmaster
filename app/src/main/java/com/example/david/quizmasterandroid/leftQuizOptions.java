package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class leftQuizOptions extends AppCompatActivity implements View.OnClickListener {

    public final int maxCatsChosen = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_quiz_options);

        TextView errorText = (TextView) findViewById(R.id.errorText);
        errorText.setText("Du måste välja fyra kategorier!");
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setClickable(false); startButton.setBackgroundColor(2);
    }



    //Start game
    public void startTheGame(View view) {
        Intent startIntent = new Intent(leftQuizOptions.this, StartGameActivity.class);

        sharedPutCategoriesToStart start = new sharedPutCategoriesToStart(startIntent);

        startIntent = start.putExtras(leftQuizOptions.this, startIntent);

        startActivity(startIntent);
    }
}
