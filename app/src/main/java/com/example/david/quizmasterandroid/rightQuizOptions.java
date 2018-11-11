package com.example.david.quizmasterandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class rightQuizOptions extends AppCompatActivity {

    private Boolean rightHanded = MainActivity.getIsRightHanded();
    private String handBool = Boolean.toString(rightHanded);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_options);

        Toast.makeText(this, handBool, Toast.LENGTH_LONG);
    }
}
