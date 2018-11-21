package com.example.david.quizmasterandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

public class StartGameActivity extends AppCompatActivity {

    //JSON grejer
    InputStream is;
    JSONObject reader;
    public JSONArray cat1json;
    public JSONArray cat2json;
    public JSONArray cat3json;
    public JSONArray cat4json;

    Intent thisIntent = getIntent();
    ConstraintLayout main;

    //Ints för att bestämma vilken kategori och fråga som ska laddas
    int stageOfGame = 0;
    int questionNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        //Hämta kategorierna
        Bundle extras = getIntent().getExtras();
        String cat1 = extras.getString("cat1");
        String cat2 = extras.getString("cat2");
        String cat3 = extras.getString("cat3");
        String cat4 = extras.getString("cat4");

        //Ladda in alla frågor som ska användas från JSON
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            cat1json = obj.getJSONArray(cat1);
            cat2json = obj.getJSONArray(cat2);
            cat3json = obj.getJSONArray(cat3);
            cat4json = obj.getJSONArray(cat4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Visa kategorinamnet
        TextView catName = (TextView) findViewById(R.id.catName); catName.setText(cat1);
        catName.setTextColor(Color.BLACK);

        //Lägg rotlayouten i en variabel som går att använda senare
        main = (ConstraintLayout) findViewById((R.id.main));
        main.setOnClickListener(continueCheck);
    }

    View.OnClickListener continueCheck = new View.OnClickListener() {
        public void onClick(View v) {
            //Gör skärmen otryckbar och ta bort element
            main.setClickable(false);   main.removeAllViews();
            putFirstQuestion();
        }
    };

    void putFirstQuestion() {
        LinearLayout theLayout = createQuestionLayout(stageOfGame);
        main.addView(theLayout);
    }

    //TODO Skapa metod som svarar på knapparnas actionlistener för att kontrollera om svaret är rätt eller fel och som även ger ++stageOfGame

    LinearLayout createQuestionLayout(int n) {
        //TODO implementera användning av questionNum
        //Välj kategori beroende på n dvs vilket steg i spelet man är
        JSONArray theCatJSON;
        if(n == 0)
            theCatJSON = cat1json;
        else if(n == 1)
            theCatJSON = cat2json;
        else if(n == 2)
            theCatJSON = cat3json;
        else
            theCatJSON = cat4json;

        //Uppdatera layouten med första frågan osv
        final LinearLayout c;
        try {
            //Skapa linearlayout för skärmen
            c = new LinearLayout(main.getContext());
            c.setOrientation(LinearLayout.VERTICAL); c.setGravity(Gravity.CENTER);
            c.setPadding(0, 200,0,0);
            c.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));


            //Skapa TextView objekt med frågan
            final TextView questionText = new TextView(main.getContext());
            questionText.setText(theCatJSON.getJSONObject(n).getString("question"));

            //Egenskaper
            questionText.setTextSize(28); questionText.setPadding(0,150,0,200);
            questionText.setGravity(Gravity.CENTER);

            //Skapa en tabell (matris) med knappar
            final TableLayout buttonCon = new TableLayout((main.getContext()));
            buttonCon.setGravity(Gravity.CENTER); buttonCon.setPadding(0,200,0,0);

            //Skapa table rows
            final TableRow tableRow1 = new TableRow(main.getContext());
            tableRow1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER);

            final TableRow tableRow2 = new TableRow(main.getContext());
            tableRow2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            tableRow2.setGravity(Gravity.CENTER);

            //Skapa knappar
            Button button1 = new Button(buttonCon.getContext()); button1.setText(theCatJSON.getJSONObject(n).getJSONObject("answers").getString("answer_1"));
            Button button2 = new Button(buttonCon.getContext()); button2.setText(theCatJSON.getJSONObject(n).getJSONObject("answers").getString("answer_2"));
            Button button3 = new Button(buttonCon.getContext()); button3.setText(theCatJSON.getJSONObject(n).getJSONObject("answers").getString("answer_3"));
            Button button4 = new Button(buttonCon.getContext()); button4.setText(theCatJSON.getJSONObject(n).getJSONObject("answers").getString("answer_4"));
            //TODO lägg till actionlistenter för alla knappar

            //Lägga knappar i 2 kolonner
            tableRow1.addView(button1);
            tableRow1.addView(button2);
            tableRow2.addView(button3);
            tableRow2.addView(button4);

            //Lägg till kolonner i table
            buttonCon.addView(tableRow1);
            buttonCon.addView(tableRow2);

            //Skapa nedräkningstext
            final TextView countdown = new TextView(main.getContext());
            countdown.setGravity(Gravity.CENTER); countdown.setTextSize(20); countdown.setPadding(0,50,0,0);
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    countdown.setText("Tid kvar: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    countdown.setText("Tiden är ute!");
                    main.setBackgroundColor(Color.RED);
                }
            }.start();

            //Lägg till alla object i linearlayout
            c.addView(questionText, 0);
            c.addView(buttonCon);
            c.addView(countdown);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
            return c;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}
