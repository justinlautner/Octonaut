package com.example.jay.Octonaut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_screen);
    }

    public void affirmative(View view){
        //User decision to start game
        Intent intent = new Intent(this, FightForLife.class);
        startActivity(intent);
    }

    public void refuse(View view){
        //User decision to avoid game
        Intent intent = new Intent(this, GameOver.class);
        startActivity(intent);
    }
}
