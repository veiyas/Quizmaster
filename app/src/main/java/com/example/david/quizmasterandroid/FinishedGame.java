package com.example.david.quizmasterandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class FinishedGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_game);

        //Beräkna och visa betyg
        putGrade();
    }

    private void putGrade() {
        //Hämta statistik och kategorier
        int[] statistics = getIntent().getExtras().getIntArray("statistics");
        ArrayList<String> cats = getIntent().getExtras().getStringArrayList("categories");

        //Hämta betyg och text elementen för att visa betyg
        RatingBar grade = findViewById(R.id.gradeBar);
        grade.setClickable(false);
        TextView gradeText = findViewById(R.id.gradeText);

        double totalQuestions = StartGameActivity.numberOfSubjects * StartGameActivity.questionPerSubject;
        double totalCorrectAnwers = 0;

        for (int i = 0; i < StartGameActivity.numberOfSubjects; ++i) {
            totalCorrectAnwers += statistics[i];
        }

        double accuracy = totalCorrectAnwers / totalQuestions;

        if (accuracy == 1) {
            grade.setRating(5);
            gradeText.setText("Perfekt!");
        } else if (accuracy < 1 && accuracy >= 0.8) {
            grade.setRating(4);
            gradeText.setText("Mycket bra!");
        } else if (accuracy < 0.8 && accuracy >= 0.6) {
            grade.setRating(3);
            gradeText.setText("Bra!");
        } else if (accuracy < 0.6 && accuracy >= 0.4) {
            grade.setRating(2);
            gradeText.setText("Helt okej!");
        } else if (accuracy < 0.4 && accuracy >= 0.2) {
            grade.setRating(1);
            gradeText.setText("Dåligt!");
        } else {
            grade.setRating(0);
            gradeText.setText("Värdelöst!");
        }

        //Hämta rätt TextView
        TextView catText = findViewById(R.id.catText);

        //Kontrollera vilken kategori som var bäst
        int slotWithHighest = 0;

        for(int i=0; i < StartGameActivity.numberOfSubjects - 1;  ++i) {
            if(statistics[i] < statistics[i+1])
                slotWithHighest = i+1;
        }
        catText.setText(cats.get(slotWithHighest));
    }
}
