package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static boolean isRightHanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chooseHand(View view) {
        if(isRightHanded == true) {
            Intent intent = new Intent(this, rightQuizOptions.class);

            startActivity(intent);
        } else {
            Intent intent = new Intent(this, leftQuizOptions.class);

            startActivity(intent);
        }
    }

    public void setRightHanded(View firstButton) {
        isRightHanded = true;

        chooseHand(firstButton);
    }

    public void setLeftHanded(View firstButton) {
        isRightHanded = false;

        chooseHand(firstButton);
    }
}
