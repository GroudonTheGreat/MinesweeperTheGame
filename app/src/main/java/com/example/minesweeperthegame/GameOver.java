package com.example.minesweeperthegame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.minesweeperthegame.MainActivity.streakCount;

public class GameOver extends AppCompatActivity {

    EditText name;
    Button submit, viewAll, cancel;
    TextView score;

    String pName;
    int check = 0;

    SQLiteDatabase database;
    String sql = "CREATE TABLE IF NOT EXISTS highscore(Name VARCHAR,Score int);";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        database = openOrCreateDatabase("Minesweeper", Context.MODE_PRIVATE, null);
        database.execSQL(sql);

        name = findViewById(R.id.name);
        score = findViewById(R.id.score);

        submit = findViewById(R.id.submit);
        viewAll = findViewById(R.id.viewAll);
        cancel = findViewById(R.id.cancel);

        score.setText("" + streakCount);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString().trim();
                if(n.length() == 0){
                    name.requestFocus();
                    return;
                }
                Cursor cursor = database.rawQuery("SELECT * FROM highscore where Name = '"+ n +"';", null);
                if(cursor.getCount() == 0){
                    database.execSQL("INSERT INTO highscore VALUES('" + name.getText() + "'," + streakCount + ");");
                }
                else{
                    cursor.moveToFirst();
                    check = cursor.getInt(1);
                    if(streakCount > check){
                        database.execSQL("UPDATE highscore SET Score = " + streakCount + " where Name = '" + n + "';");
                    }
                    else{
                        showMessage("Alert","Record with higher Streak exists\nPlease use a different Name");
                    }
                }
                showMessage("Success", "Record added");
                Intent intent = new Intent(GameOver.this, MainActivity.class);
                startActivity(intent);
            }
        });

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = database.rawQuery("SELECT * FROM highscore;", null);
                if (cursor.getCount() == 0) {
                    showMessage("Error", "No records found");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (cursor.moveToNext()) {
                    buffer.append("Name :\t\t" + cursor.getString(0) + "\n");
                    buffer.append("Score :\t" + cursor.getInt(1) + "\n\n");
                }
                showMessage("Highscores", buffer.toString());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOver.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
