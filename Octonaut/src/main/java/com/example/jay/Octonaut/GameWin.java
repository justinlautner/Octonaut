package com.example.jay.Octonaut;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class GameWin extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_win);
    }

    public void onButtonPress(View view){
        //Reset game to home screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}