package com.example.david.quizmasterandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

import java.util.ArrayList;

public class sharedPutCategoriesToStart extends AppCompatActivity {
    public final int maxCatsChosen = 4;
    private Intent intent;

    sharedPutCategoriesToStart(Intent input) {
        intent = input;
    }

    public Intent putExtras(Activity theActivity, Intent intent) {
        ArrayList<Switch> theSwitches = new ArrayList<>();
        theSwitches.add((Switch) theActivity.findViewById(R.id.switch1));
        theSwitches.add((Switch) theActivity.findViewById(R.id.switch2));
        theSwitches.add((Switch) theActivity.findViewById(R.id.switch3));
        theSwitches.add((Switch) theActivity.findViewById(R.id.switch4));
        theSwitches.add((Switch) theActivity.findViewById(R.id.switch5));
        theSwitches.add((Switch) theActivity.findViewById(R.id.switch6));

        ArrayList<Switch> falseSwitches = new ArrayList<>();

        for(int i=0; i < theSwitches.size(); i++) {
            if(!theSwitches.get(i).isChecked()) {
                falseSwitches.add(theSwitches.get(i));
            }
        }

        for(int i=0; i < falseSwitches.size(); i++) {
            theSwitches.remove(falseSwitches.get(i));
        }

        for(int i=0; i < maxCatsChosen; i++) {
            String catNumber = "cat" + Integer.toString(i+1);
            intent.putExtra(catNumber, theSwitches.get(i).getText());
        }

        return intent;
    }
}
