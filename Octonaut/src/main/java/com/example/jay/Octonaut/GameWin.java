package com.example.jay.Octonaut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class GameWin extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_win);
    }

    public void onButtonPress(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}