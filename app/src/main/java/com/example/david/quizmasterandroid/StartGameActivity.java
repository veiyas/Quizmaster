package com.example.david.quizmasterandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class StartGameActivity extends AppCompatActivity {


    //JSON grejer
    private JSONArray cat1json;
    private JSONArray cat2json;
    private JSONArray cat3json;
    private JSONArray cat4json;

    //Activitet för att starta om spelet senare
    private Activity restartGame = getParent();

    //Rotlayouten för att kunna ändra innehållet dynamiskt
    private ConstraintLayout main;
    private Button continueButton;

    //Countdown
    private CountDownTimer mCountDownTimer;
    private CountDownTimer tension;

    //Knappar
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    private Button pressedtLbR;

    private int pressedGray = Color.rgb(173, 179, 188);

    //Begränsningar
    public static final int questionPerSubject = 5;
    public static final int numberOfSubjects = 4;
    boolean gameIsDone = false;
    boolean timeOut = false;

    //Spåra statistik
    private int[] numCorrect;
    private int whichQuestion = 1;

    //Nedräknigstext
    private TextView countdown;
    private boolean answerPressed = false;

    //Ints för att bestämma vilken kategori och fråga som ska laddas
    private int subjectNum = 0;
    private int questionNum = 0;
    boolean endOfGame = false;

    //Kategori namn
    private String cat1;
    private String cat2;
    private String cat3;
    private String cat4;

    //För att avsluta spelet och skicka med statistik
    Intent finishGameIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        //Hämta kategorierna
        Bundle extras = getIntent().getExtras();
        cat1 = extras.getString("cat1");
        cat2 = extras.getString("cat2");
        cat3 = extras.getString("cat3");
        cat4 = extras.getString("cat4");

        pressedtLbR = (Button) getLayoutInflater().inflate(R.layout.tl_pressed, null);

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
        //Initialisera statistik array
        numCorrect = createNumCorrectArray();
        presentCategory();
    }

    private void presentCategory() {
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/amaranth.ttf");
        setContentView(R.layout.activity_start_game);

        String subjectTitle;
        if(subjectNum == 0)
            subjectTitle = cat1;
        else if(subjectNum == 1)
            subjectTitle = cat2;
        else if(subjectNum == 2)
            subjectTitle = cat3;
        else
            subjectTitle = cat4;

        //Visa kategorinamnet
        TextView catName = (TextView) findViewById(R.id.catName); catName.setText(subjectTitle);
        catName.setTextColor(Color.BLACK);
        catName.setTypeface(tf);

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
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/amaranth.ttf");
        //Initialisera main och answerPressed
        answerPressed = false;
        timeOut = false;
        main.removeAllViews();

        //Välj kategori beroende på n dvs vilket steg i spelet man är
        JSONArray theCatJSON = pickCategory(subjectNum);

        //Uppdatera layouten med första frågan osv
        final LinearLayout c;
        try {
            //Skapa linearlayout för skärmen
            c = new LinearLayout(main.getContext());
            c.setOrientation(LinearLayout.VERTICAL); c.setGravity(Gravity.CENTER);
            c.setPadding(0, 150,0,0);
            c.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            //Skapa TextView objekt med frågan och egenskaper
            final TextView questionText = new TextView(main.getContext()); //questionText.setHeight(400);
            questionText.setText(theCatJSON.getJSONObject(questionNum).getString("question"));
            questionText.setTextSize(27); questionText.setPadding(0,40,0,50);
            questionText.setGravity(Gravity.CENTER); questionText.setHeight(400);
            questionText.setTextColor(Color.parseColor("#313131"));
            questionText.setTypeface(tf);

            //Skapa table rows
            final TableRow tableRow1 = new TableRow(main.getContext());
            tableRow1.setGravity(Gravity.CENTER);

            final TableRow tableRow2 = new TableRow(main.getContext());
            tableRow2.setGravity(Gravity.CENTER);

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.leftMargin = 7; params.topMargin = 7; params.bottomMargin = 7; params.rightMargin = 7;

            //Skapa en tabell (matris) med knappar
            final TableLayout buttonCon = new TableLayout((main.getContext()));
            buttonCon.setGravity(Gravity.CENTER); buttonCon.setPadding(0,100,0,50);

            //Skapa alla knappar (button1, osv)
            initializeAnswerButtons(theCatJSON);

            //Lägga knappar i 2 kolonner
            tableRow1.addView(button1, params);
            tableRow1.addView(button2, params);
            tableRow2.addView(button3, params);
            tableRow2.addView(button4, params);

            //Lägg till kolonner i table
            buttonCon.addView(tableRow1);
            buttonCon.addView(tableRow2);

            //Skapa nedräkningstext
            countdown = new TextView(main.getContext());
            countdown.setGravity(Gravity.CENTER); countdown.setTextSize(24);
            countdown.setPadding(0,30,0,10);
            countdown.setTypeface(tf);

            mCountDownTimer = new CountDownTimer(15000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    countdown.setText("Tid kvar: " + (millisUntilFinished / 1000));
                }
                @Override
                public void onFinish() {
                    try {
                        String correctAnswer = pickCategory(subjectNum).getJSONObject(questionNum).getString("correct");
                        timeOut = true;
                        setButtonsClickable(false);
                        labelIncorrectAnswer();
                        continueTheGame();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            mCountDownTimer.start();

            //Skapa en "nästa fråga" knapp
            // button1 = (Button) getLayoutInflater().inflate(R.layout.trbr_button, null);
            continueButton = (Button) getLayoutInflater().inflate(R.layout.next_question, null);
            continueButton.setOnClickListener(nextQuestion); continueButton.setGravity(Gravity.CENTER);
            continueButton.setVisibility(View.INVISIBLE);
            continueButton.setPadding(0, 0, 0, 20);
            continueButton.setTextColor(Color.WHITE); continueButton.setText("Nästa Fråga");
            continueButton.setTextSize(18);

            TableRow.LayoutParams paramsNextButton = new TableRow.LayoutParams();
            paramsNextButton.leftMargin = 20; paramsNextButton.topMargin = 20;
            paramsNextButton.bottomMargin = 20; paramsNextButton.rightMargin = 20;

            //Vilken fråga du är på
            TextView questionProgress = new TextView(main.getContext()); questionProgress.setGravity(Gravity.CENTER);
            questionProgress.setText(whichQuestion + "/" + (questionPerSubject*numberOfSubjects)); questionProgress.setPadding(0,0,0,150);

            //Lägg till alla object i linearlayout och sen i main ConstraintLayout
            c.addView(questionProgress);
            c.addView(questionText);
            c.addView(countdown);
            c.addView(buttonCon);
            c.addView(continueButton, paramsNextButton);

            main.addView(c);
            ++whichQuestion;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener questionCheck = new View.OnClickListener() {
        public void onClick(final View v) {
            //Stäng av timern för frågan
            mCountDownTimer.cancel();

            //Hämta valda svaret (knappen)
            final Button choice = findViewById(v.getId());

            v.setPressed(true);
            //Skapa spänning med en timout
            tension = new CountDownTimer(600, 5)
            {
                Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/amaranth.ttf");
                public void onTick(long millisUntilFinished) {
                    setButtonsClickable(false);
                    v.setPressed(true);
                }
                public void onFinish() {
                    if(correctAnswer(choice)) {
                        v.setPressed(false);
                        v.setSelected(true);
                        countdown.setText("Rätt svar!");
                        countdown.setTextColor(Color.parseColor("#207C20"));
                        numCorrect[subjectNum]++;
                    } else {
                        v.setPressed(true);
                        v.setSelected(true);
                        labelIncorrectAnswer();
                    }

                    if(subjectNum == numberOfSubjects - 1 && questionNum == questionPerSubject - 1) {
                        finishGameIntent = new Intent(StartGameActivity.this, FinishedGame.class);
                        //Lägg till statistik om accuracy
                        finishGameIntent.putExtra("statistics", numCorrect);

                        //Kategorier för att avgöra vilken kategori som var bäst
                        ArrayList<String> theCats = new ArrayList<>();
                        theCats.add(0, cat1); theCats.add(1, cat2);theCats.add(2, cat3); theCats.add(3, cat4);
                        finishGameIntent.putExtra("categories", theCats);

                        continueButton.setText("Avsluta spelet");
                        gameIsDone = true;
                    }
                    continueTheGame();
                }
            };
            tension.start();
        }
    };

    private void labelIncorrectAnswer() {
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/amaranth.ttf");

        String correctAnswer = null;
        try {
            correctAnswer = pickCategory(subjectNum).getJSONObject(questionNum).getString("correct");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (button1.getText().equals(correctAnswer))
            button1.setSelected(true);
        else if (button2.getText().equals(correctAnswer))
            button2.setSelected(true);
        else if (button3.getText().equals(correctAnswer))
            button3.setSelected(true);
        else if (button4.getText().equals(correctAnswer))
            button4.setSelected(true);
        if (timeOut){
            countdown.setText("Tiden är ute!");
            countdown.setTextColor(Color.parseColor("#B22E2E"));
    }
        else
            countdown.setText("Fel svar!");
            countdown.setTextColor(Color.parseColor("#B22E2E"));
    }

    private void continueTheGame() {
        //Byt till nästa fråga
        ++questionNum;

        //Ändra text på continue button om kategorin ska bytas
        if(questionNum == questionPerSubject && !gameIsDone)
            continueButton.setText("Nästa kategori");

        continueButton.setVisibility(View.VISIBLE);
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

    View.OnClickListener nextQuestion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //KOlla om spelet är slut
            if(subjectNum == numberOfSubjects - 1 && questionNum == questionPerSubject) {
                startActivity(finishGameIntent);
            }
            else if(questionNum == questionPerSubject) {
                subjectNum++;
                questionNum = 0;
                presentCategory();
            }
            else
                createQuestionLayout();
        }
    };

    @SuppressLint("ResourceType")
    private void initializeAnswerButtons(JSONArray theCatJSON) throws JSONException {
        //Skapa knappar
        button1 = (Button) getLayoutInflater().inflate(R.layout.trbr_button, null); button1.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_1"));
        button2 = (Button) getLayoutInflater().inflate(R.layout.tlbr, null); button2.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_2"));
        button3 = (Button) getLayoutInflater().inflate(R.layout.tlbr, null); button3.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_3"));
        button4 = (Button) getLayoutInflater().inflate(R.layout.trbr_button, null); button4.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_4"));
        setButtonsClickable(true);

        button1.setTextColor(Color.LTGRAY);
        button2.setTextColor(Color.LTGRAY);
        button3.setTextColor(Color.LTGRAY);
        button4.setTextColor(Color.LTGRAY);

        //lägg till actionlistenter och ID för alla knappar
        button1.setOnClickListener(questionCheck); button1.setId(1);
        button2.setOnClickListener(questionCheck); button2.setId(2);
        button3.setOnClickListener(questionCheck); button3.setId(3);
        button4.setOnClickListener(questionCheck); button4.setId(4);
    }

    private void setButtonsClickable(boolean c) {
        if(c) {
            button1.setClickable(true);
            button2.setClickable(true);
            button3.setClickable(true);
            button4.setClickable(true);
        }
        else {
            button1.setClickable(false);
            button2.setClickable(false);
            button3.setClickable(false);
            button4.setClickable(false);
        }
    }

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
        temp[1] = 0;
        temp[2] = 0;
        temp[3] = 0;

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
