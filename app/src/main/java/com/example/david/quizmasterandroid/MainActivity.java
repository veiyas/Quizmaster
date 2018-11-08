package com.example.david.quizmasterandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public boolean isRightHanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setRightHanded(View firstButton) {
        isRightHanded = true;
        Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
    }

    public void setLeftHanded(View firstButton) {
        isRightHanded = false;
        Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
    }
}