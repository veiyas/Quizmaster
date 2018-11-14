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

    public void startTest(View view) {
        Intent intent = new Intent(rightQuizOptions.this, StartGameActivity.class);
        startActivity(intent);
    }
}
