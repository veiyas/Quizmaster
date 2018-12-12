package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public static boolean isRightHanded = true;
    private ImageView rightHand;
    private ImageView leftHand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rightHand = (ImageView) findViewById(R.id.rightHanded);
        leftHand = (ImageView) findViewById(R.id.leftHanded);
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

        rightHand.setPressed(true);

        chooseHand(firstButton);
    }

    public void setLeftHanded(View firstButton) {
        isRightHanded = false;

        chooseHand(firstButton);
    }
}
