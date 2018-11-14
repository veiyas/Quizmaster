package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class rightQuizOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right_quiz_options);
    }

    public void startTheGame(View view) {
        Intent startIntent = new Intent(rightQuizOptions.this, StartGameActivity.class);

        sharedPutCategoriesToStart start = new sharedPutCategoriesToStart(startIntent);

        startIntent = start.putExtras(rightQuizOptions.this, startIntent);

        startActivity(startIntent);
    }
}
