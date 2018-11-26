package com.example.david.quizmasterandroid;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;

public class StartGameActivity extends AppCompatActivity {
    //JSON grejer
    private JSONArray cat1json;
    private JSONArray cat2json;
    private JSONArray cat3json;
    private JSONArray cat4json;

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

    //Begränsningar
    public static final int questionPerSubject = 2;
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
            final TextView questionText = new TextView(main.getContext()); questionText.setHeight(400);
            questionText.setText(theCatJSON.getJSONObject(questionNum).getString("question"));
            questionText.setTextSize(27); questionText.setPadding(0,40,0,50);
            questionText.setGravity(Gravity.CENTER);

            //Skapa table rows
            final TableRow tableRow1 = new TableRow(main.getContext());
            tableRow1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            tableRow1.setGravity(Gravity.CENTER); tableRow1.setWeightSum(100);

            final TableRow tableRow2 = new TableRow(main.getContext());
            tableRow2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            tableRow2.setGravity(Gravity.CENTER); tableRow2.setWeightSum(100);

            //Skapa en tabell (matris) med knappar
            final TableLayout buttonCon = new TableLayout((main.getContext()));
            buttonCon.setGravity(Gravity.CENTER); buttonCon.setPadding(0,100,0,80);

            //Skapa alla knappar (button1, osv)
            initializeAnswerButtons(theCatJSON);

            //Lägga knappar i 2 kolonner
            tableRow1.addView(button1);
            tableRow1.addView(button2);
            tableRow2.addView(button3);
            tableRow2.addView(button4);

            //Lägg till kolonner i table
            buttonCon.addView(tableRow1);
            buttonCon.addView(tableRow2);

            //Skapa nedräkningstext
            countdown = new TextView(main.getContext()); countdown.setHeight(280);
            countdown.setGravity(Gravity.CENTER); countdown.setTextSize(20); countdown.setPadding(0,0,0,0);

            mCountDownTimer = new CountDownTimer(10000,1000) {
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
            continueButton = new Button(c.getContext()); continueButton.setText("Nästa Fråga");
            continueButton.setOnClickListener(nextQuestion); continueButton.setGravity(Gravity.CENTER);
            continueButton.setVisibility(View.INVISIBLE); continueButton.setHeight(300);
            continueButton.setPadding(0, 0, 0, 30);

            //Vilken fråga du är på
            TextView questionProgress = new TextView(main.getContext()); questionProgress.setGravity(Gravity.CENTER);
            questionProgress.setText(whichQuestion + "/" + (questionPerSubject*numberOfSubjects)); questionProgress.setPadding(0,0,0,150);

            //Lägg till alla object i linearlayout och sen i main ConstraintLayout
            c.addView(questionProgress);
            c.addView(questionText);
            c.addView(countdown);
            c.addView(buttonCon);
            c.addView(continueButton);

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

            //Färga knappen gul
            answerPressed = true;

            //Hämta valda svaret (knappen)
            final Button choice = findViewById(v.getId());

            //Skapa spänning med en timout
            tension = new CountDownTimer(2500, 10)
            {
                public void onTick(long millisUntilFinished) {
                    v.setBackgroundColor(Color.YELLOW);
                    setButtonsClickable(false);
                }
                public void onFinish() {
                    if(correctAnswer(choice)) {
                        countdown.setText("Rätt svar!");
                        v.setBackgroundColor(Color.GREEN);
                        numCorrect[subjectNum]++;
                    } else {
                        v.setBackgroundColor(Color.RED);
                        labelIncorrectAnswer();
                    }

                    if(subjectNum == numberOfSubjects - 1 && questionNum == questionPerSubject - 1) {
                        finishGameIntent = new Intent(StartGameActivity.this, FinishedGame.class);
                        //Lägg till statistik om accuracy
                        finishGameIntent.putExtra("statistics", numCorrect);

                        //Kategorier för att avgöra vilken kategori som var bäst
                        ArrayList<String> theCats = new ArrayList<>();
                        theCats.add(0, cat1); theCats.add(1, cat2);theCats.add(2, cat3);theCats.add(3, cat4);
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
        String correctAnswer = null;
        try {
            correctAnswer = pickCategory(subjectNum).getJSONObject(questionNum).getString("correct");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(button1.getText().equals(correctAnswer))
            button1.setBackgroundColor(Color.GREEN);
        else if(button2.getText().equals(correctAnswer))
            button2.setBackgroundColor(Color.GREEN);
        else if(button3.getText().equals(correctAnswer))
            button3.setBackgroundColor(Color.GREEN);
        else if(button4.getText().equals(correctAnswer))
            button4.setBackgroundColor(Color.GREEN);
        if(timeOut)
            countdown.setText("Tiden är ute! \n Rätt svar är: \n" + correctAnswer);
        else
            countdown.setText("Fel svar! \n Rätt svar är: \n" + correctAnswer);
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
        button1 = new Button(main.getContext()); button1.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_1"));
        button2 = new Button(main.getContext()); button2.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_2"));
        button3 = new Button(main.getContext()); button3.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_3"));
        button4 = new Button(main.getContext()); button4.setText(theCatJSON.getJSONObject(questionNum).getJSONObject("answers").getString("answer_4"));

        button1.setWidth(500); button2.setWidth(500); button3.setWidth(500); button4.setWidth(500);
        button1.setHeight(200); button2.setHeight(200); button3.setHeight(200); button4.setHeight(200);

        setButtonsClickable(true);

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
