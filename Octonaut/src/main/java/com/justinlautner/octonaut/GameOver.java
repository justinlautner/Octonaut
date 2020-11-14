package com.justinlautner.octonaut;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class GameOver extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
    }

    public void onButtonPress(View view){
        //Reset game to home screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
