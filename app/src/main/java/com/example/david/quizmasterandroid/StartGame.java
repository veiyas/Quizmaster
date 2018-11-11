package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static android.support.v4.content.ContextCompat.startActivity;

public class StartGame extends AppCompatActivity {

    @Override
    public Intent getGameIntent() {
        return intent;
    }

    public Intent intent = new Intent(this, quizGame.class);
}
