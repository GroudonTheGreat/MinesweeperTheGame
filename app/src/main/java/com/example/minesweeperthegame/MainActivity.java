package com.example.minesweeperthegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Random;

class Node{
    int x,y;
    Node next;

    Node(int x, int y){
        this.x = x;
        this.y = y;
        next = null;
    }
}

class Q{
    Node head, newNode;

    Q(){
        head = newNode = null;
    }

    boolean isEmpty(){
        if(head == null)
            return true;
        return false;
    }

    void push(Node obj){
        newNode = new Node(obj.x, obj.y);
        if(head == null){
            head = newNode;
        }
        else{
            Node cn = head;
            while(cn.next != null){
                cn = cn.next;
            }
            cn.next = newNode;
        }
    }

    void pop(){
        if(!isEmpty()){
            Node cn = head;
            head = head.next;
        }
    }

    Node front(){
        if(!isEmpty())
            return head;
        return null;
    }
}

public class MainActivity extends AppCompatActivity {

    int btnCountRow = 8, btnCountCol = 6;
    public static int streakCount = 0;
    int buttonLeft = btnCountRow * btnCountCol;

    Button button[][] = new Button[btnCountRow][btnCountCol];
    ImageButton smile;

    int bombCount = 8;
    int bomb[] = new int[bombCount];
    Random rand = new Random();

    Q queue = new Q();

    SQLiteDatabase database;
    String sql = "CREATE TABLE IF NOT EXISTS highscore(Name VARCHAR,Score int);";

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        database = openOrCreateDatabase("Minesweeper", Context.MODE_PRIVATE, null);
        database.execSQL(sql);

        int id = item.getItemId();

        switch (id){
            case R.id.HighScores:
                Cursor cursor = database.rawQuery("SELECT * FROM highscore;", null);
                if (cursor.getCount() == 0) {
                    showMessage("Error", "No records found");
                    break;
                }
                StringBuffer buffer = new StringBuffer();
                while (cursor.moveToNext()) {
                    buffer.append("Name :\t\t" + cursor.getString(0) + "\n");
                    buffer.append("Score :\t" + cursor.getInt(1) + "\n\n");
                }
                showMessage("Highscores", buffer.toString());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smile = findViewById(R.id.smile);

        button[0][0] = findViewById(R.id.button00);
        button[0][1] = findViewById(R.id.button01);
        button[0][2] = findViewById(R.id.button02);
        button[0][3] = findViewById(R.id.button03);
        button[0][4] = findViewById(R.id.button04);
        button[0][5] = findViewById(R.id.button05);

        button[1][0] = findViewById(R.id.button10);
        button[1][1] = findViewById(R.id.button11);
        button[1][2] = findViewById(R.id.button12);
        button[1][3] = findViewById(R.id.button13);
        button[1][4] = findViewById(R.id.button14);
        button[1][5] = findViewById(R.id.button15);

        button[2][0] = findViewById(R.id.button20);
        button[2][1] = findViewById(R.id.button21);
        button[2][2] = findViewById(R.id.button22);
        button[2][3] = findViewById(R.id.button23);
        button[2][4] = findViewById(R.id.button24);
        button[2][5] = findViewById(R.id.button25);

        button[3][0] = findViewById(R.id.button30);
        button[3][1] = findViewById(R.id.button31);
        button[3][2] = findViewById(R.id.button32);
        button[3][3] = findViewById(R.id.button33);
        button[3][4] = findViewById(R.id.button34);
        button[3][5] = findViewById(R.id.button35);

        button[4][0] = findViewById(R.id.button40);
        button[4][1] = findViewById(R.id.button41);
        button[4][2] = findViewById(R.id.button42);
        button[4][3] = findViewById(R.id.button43);
        button[4][4] = findViewById(R.id.button44);
        button[4][5] = findViewById(R.id.button45);

        button[5][0] = findViewById(R.id.button50);
        button[5][1] = findViewById(R.id.button51);
        button[5][2] = findViewById(R.id.button52);
        button[5][3] = findViewById(R.id.button53);
        button[5][4] = findViewById(R.id.button54);
        button[5][5] = findViewById(R.id.button55);

        button[6][0] = findViewById(R.id.button60);
        button[6][1] = findViewById(R.id.button61);
        button[6][2] = findViewById(R.id.button62);
        button[6][3] = findViewById(R.id.button63);
        button[6][4] = findViewById(R.id.button64);
        button[6][5] = findViewById(R.id.button65);

        button[7][0] = findViewById(R.id.button70);
        button[7][1] = findViewById(R.id.button71);
        button[7][2] = findViewById(R.id.button72);
        button[7][3] = findViewById(R.id.button73);
        button[7][4] = findViewById(R.id.button74);
        button[7][5] = findViewById(R.id.button75);

        reset();

        for(int i = 0; i < btnCountRow; i++){
            for(int j = 0; j < btnCountCol; j++) {
                final int finalI = i;
                final int finalJ = j;
                button[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isBomb(finalI, finalJ)) {
                            gameover();
                        } else {
                            button[finalI][finalJ].setClickable(false);
                            buttonLeft--;
                            int check = ifNumber(finalI,finalJ);
                            if(check != 0){
                                button[finalI][finalJ].setText("" + check);
                            } else{
                                button[finalI][finalJ].setText("" + 0);
                                queue.push(new Node(finalI,finalJ));
                                spread();
                            }
                            if(buttonLeft == bombCount){
                                streakCount++;
                                reset();
                                Toast.makeText(MainActivity.this, "Current Streak : " + streakCount, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }


        smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
    }

    private void spread() {

        int finalI = 0,finalJ = 0;

        while (!queue.isEmpty()){

            finalI = queue.front().x;
            finalJ = queue.front().y;
            queue.pop();

            fun(finalI - 1,finalJ - 1);
            fun(finalI,finalJ - 1);
            fun(finalI + 1,finalJ - 1);
            fun(finalI + 1,finalJ);
            fun(finalI + 1,finalJ + 1);
            fun(finalI,finalJ + 1);
            fun(finalI - 1,finalJ + 1);
            fun(finalI - 1,finalJ);

        }
    }

    private int ifNumber(int finalI, int finalJ) {
        int count = 0;
        if(isBomb(finalI,finalJ - 1))
            count++;
        if(isBomb(finalI + 1,finalJ))
            count++;
        if(isBomb(finalI,finalJ + 1))
            count++;
        if(isBomb(finalI - 1,finalJ))
            count++;
        if(isBomb(finalI + 1,finalJ - 1))
            count++;
        if(isBomb(finalI + 1,finalJ + 1))
            count++;
        if(isBomb(finalI - 1,finalJ + 1))
            count++;
        if(isBomb(finalI - 1,finalJ - 1))
            count++;
        return count;
    }

    private void gameover() {
        for(int i = 0; i < bombCount; i++){
            int x = bomb[i] / btnCountCol;
            int y = bomb[i] - x * btnCountCol;
            button[x][y].setClickable(false);
            button[x][y].setText("B");
        }
        Intent intent = new Intent(MainActivity.this, GameOver.class);
        startActivity(intent);
    }

    private void fun(int x, int y){
        if((x >= 0) && (x < btnCountRow) && (y >= 0) && (y < btnCountCol)){
            if(button[x][y].isClickable()){

                button[x][y].setClickable(false);
                buttonLeft--;

                if(ifNumber(x,y) == 0){
                    button[x][y].setText("" + 0);
                    queue.push(new Node(x,y));
                }
                else{
                    button[x][y].setText("" + ifNumber(x,y));
                }
            }
        }
    }

    private boolean isBomb(int x, int y){
        if(x >= 0 && x < btnCountRow && y >= 0 && y < btnCountCol){
            for(int i = 0; i < bombCount; i++){
                if(bomb[i] == x * btnCountCol + y)
                    return true;
            }
        }
        return false;
    }

    public void reset() {
        for (int i = 0; i < bombCount; i++) {
            int temp = rand.nextInt(36);
            for (int j = 0; j < i; j++) {
                if (bomb[j] == temp) {
                    temp = rand.nextInt(36);
                    j = 0;
                }
            }
            bomb[i] = temp;
        }

        for (int i = 0; i < btnCountRow; i++) {
            for (int j = 0; j < btnCountCol; j++) {
                button[i][j].setClickable(true);
                button[i][j].setText("");
            }
        }

        buttonLeft = btnCountRow * btnCountCol;
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
