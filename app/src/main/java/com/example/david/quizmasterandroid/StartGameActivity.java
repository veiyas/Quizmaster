package com.example.david.quizmasterandroid;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class StartGameActivity extends AppCompatActivity {

    //JSON grejer
    private InputStream is;
    private JSONObject reader;
    private JSONArray cat1json;
    private JSONArray cat2json;
    private JSONArray cat3json;
    private JSONArray cat4json;

    //Rotlayouten för att kunna ändra innehållet dynamiskt
    private ConstraintLayout main;
    private Button continueButton;

    //Begränsningar
    private final int questionPerSubject = 2;
    private final int numberOfSubjects = 4;

    //Nedräknigstext
    private TextView countdown;
    private boolean answerPressed = false;

    //Ints för att bestämma vilken kategori och fråga som ska laddas
    private int subjectNum = 0;
    private int questionNum = 0;
    boolean endOfGame = false;

    //Spåra statistik
    private final int questionPerCat = 2;
    private int[] numCorrect = createNumCorrectArray();

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

        //TODO inte hårdkoda första skärmen, implementera dynamiskt användbar kategoripresentatör
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
            createQuestionLayout();
        }
    };

    @SuppressLint("ResourceType")
    public void createQuestionLayout() {
        //Initialisera main och answerPressed
        answerPressed = false;
        main.removeAllViews();

        //Välj kategori beroende på n dvs vilket steg i spelet man är
        JSONArray theCatJSON = pickCategory(subjectNum);

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
            questionText.setText(theCatJSON.getJSONObject(questionNum).getString("question"));

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
            Button button1 = new Button(buttonCon.getContext()); button1.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_1"));
            Button button2 = new Button(buttonCon.getContext()); button2.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_2"));
            Button button3 = new Button(buttonCon.getContext()); button3.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_3"));
            Button button4 = new Button(buttonCon.getContext()); button4.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_4"));

            //lägg till actionlistenter och ID för alla knappar
            button1.setOnClickListener(questionCheck); button1.setId(1);
            button2.setOnClickListener(questionCheck); button2.setId(2);
            button3.setOnClickListener(questionCheck); button3.setId(3);
            button4.setOnClickListener(questionCheck); button4.setId(4);

            //Lägga knappar i 2 kolonner
            tableRow1.addView(button1);
            tableRow1.addView(button2);
            tableRow2.addView(button3);
            tableRow2.addView(button4);

            //Lägg till kolonner i table
            buttonCon.addView(tableRow1);
            buttonCon.addView(tableRow2);

            //Skapa nedräkningstext
            countdown = new TextView(main.getContext());
            countdown.setGravity(Gravity.CENTER); countdown.setTextSize(20); countdown.setPadding(0,50,0,0);
//            new CountDownTimer(10000, 1000) {
//
//                public void onTick(long millisUntilFinished) {
//                    if(!answerPressed)
//                        countdown.setText("Tid kvar: " + millisUntilFinished / 1000);
//                    else
//                        countdown.setText(" ");
//                }
//                public void onFinish() {
//                    if(!answerPressed)
//                        countdown.setText("Tiden är ute!");
//                    else
//                        countdown.setText(" ");
//                }
//            }.start();

            //Skapa en "nästa fråga" knapp
            continueButton = new Button(c.getContext()); continueButton.setText("Nästa Fråga");
            continueButton.setOnClickListener(nextQuestion);
            continueButton.setVisibility(View.INVISIBLE);

            //Lägg till alla object i linearlayout och sen i main ConstraintLayout
            c.addView(questionText, 0);
            c.addView(countdown);
            c.addView(buttonCon);
            c.addView(continueButton);

            main.addView(c);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener questionCheck = new View.OnClickListener() {
        public void onClick(final View v) {
            answerPressed = true;
            final Button choice = findViewById(v.getId());

            //Skapa spänning med en timout
            new CountDownTimer(2500, 10)
            {
                public void onTick(long millisUntilFinished) {
                    v.setBackgroundColor(Color.YELLOW);
                }
                public void onFinish() {
                    if(correctAnswer(choice)) {
                        v.setBackgroundColor(Color.GREEN);
                        numCorrect[subjectNum]++;
                    } else {
                        v.setBackgroundColor(Color.RED);
                    }
                    continueButton.setVisibility(View.VISIBLE);

                    //Byt till nästa fråga
                    ++questionNum;

                    //KOlla om spelet är slut
                    if(subjectNum == numberOfSubjects - 1 && questionNum == questionPerSubject) {
                        //Starta ny aktivitet
                        Toast.makeText(StartGameActivity.this, "slut", Toast.LENGTH_LONG).show();
                        //TODO SKapa ny aktivitet för att avsluta spelet och visa statistik
                    }
                    //Kontrollera om kategorin måste bytas
                    else if(questionNum == questionPerSubject) {
                        subjectNum++;
                        questionNum = 0;
                        Toast.makeText(StartGameActivity.this, "subhectNum++", Toast.LENGTH_LONG).show();
                    }
                }
            }.start();
        }

        //Kontrollera om det var rätt svar
        private boolean correctAnswer(Button id) {
            try {
                String correctAnswer = pickCategory(subjectNum).getJSONObject(questionNum).getString("correct");

                if(id.getText().equals(correctAnswer))
                    return true;
                else
                    return false;
            }
            catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
    };

    //TODO implementera kategoripresentationsskärm
    View.OnClickListener nextQuestion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createQuestionLayout();
        }
    };

    private JSONArray pickCategory(int n) {
        if(n == 0)
            return cat1json;
        else if(n == 1)
            return cat2json;
        else if(n == 2)
            return cat3json;
        else
            return cat4json;
    }

    private int[] createNumCorrectArray() {
        int[] temp = new int[4];
        temp[0] = 0;
        temp[1] = 1;
        temp[2] = 2;
        temp[3] = 3;

        return temp;
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
