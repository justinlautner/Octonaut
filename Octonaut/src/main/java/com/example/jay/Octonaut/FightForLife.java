package com.example.jay.Octonaut;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class FightForLife extends AppCompatActivity {

    GameView gameView;
    Paint drawPaint = new Paint();
    Paint recPaint = new Paint();
    Paint textPaint = new Paint();
    int backgroundColor = Color.BLACK;
    Bitmap enemy1, enemy2, enemy3, enemy4;
    Bitmap backgroundStar;
    Bitmap squidHero;
    Bitmap waterBlast, waterBlast2, waterBlast3;
    Bitmap explosion;
    int enemy1X, enemy1Y = -200, enemy1YSpeed = 10, enemy1XOld, enemy1YOld;
    int enemy2X, enemy2Y = -300, enemy2YSpeed = 10, enemy2XOld, enemy2YOld;
    int enemy3X, enemy3Y = -400, enemy3YSpeed = 10, enemy3XOld, enemy3YOld;
    int enemy4X, enemy4Y = -500, enemy4YSpeed = 10, enemy4XOld, enemy4YOld;
    int backgroundStarX = 100, backgroundStarY = 100;
    int squidHeroX, squidHeroY;
    int waterBlastX, waterBlastY, waterBlastXEnd, waterBlastYEnd;
    int waterBlast2X, waterBlast2Y;
    int waterBlast3X, waterBlast3Y;
    double waterBlastXVel, waterBlastYVel, waterBlast2XVel, waterBlast2YVel, waterBlast3XVel, waterBlast3YVel;
    Rect enemy1Rect, enemy2Rect, enemy3Rect, enemy4Rect, waterBlastRect, waterBlastRect2, waterBlastRect3;
    SoundPool waterSplashSound, gameOverSound, winGameSound, bombSound;
    int waterSplashID, gameOverID, winGameID, bombID;

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        gameView = new GameView(this);
        this.setContentView(gameView);

        backgroundStar = BitmapFactory.decodeResource(getResources(), R.drawable.star_sprite);
        squidHero = BitmapFactory.decodeResource(getResources(), R.drawable.space_squid);
        waterBlast = BitmapFactory.decodeResource(getResources(), R.drawable.water_sprite);
        waterBlast2 = BitmapFactory.decodeResource(getResources(), R.drawable.water_sprite);
        waterBlast3 = BitmapFactory.decodeResource(getResources(), R.drawable.water_sprite);
        enemy1 = BitmapFactory.decodeResource(getResources(), R.drawable.alien_saucer);
        enemy2 = BitmapFactory.decodeResource(getResources(), R.drawable.alien_saucer);
        enemy3 = BitmapFactory.decodeResource(getResources(), R.drawable.alien_saucer);
        enemy4 = BitmapFactory.decodeResource(getResources(), R.drawable.alien_saucer);
        explosion = BitmapFactory.decodeResource(getResources(), R.drawable.explosion_sprite);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        waterSplashSound = new SoundPool.Builder()
                .setMaxStreams(20)
                .build();
        gameOverSound = new SoundPool.Builder()
                .setMaxStreams(20)
                .build();
        winGameSound = new SoundPool.Builder()
                .setMaxStreams(20)
                .build();
        bombSound = new SoundPool.Builder()
                .setMaxStreams(20)
                .build();
        AssetManager assetManager = getAssets();
        try{
            AssetFileDescriptor descriptor = assetManager.openFd("water_splash.wav");
            waterSplashID = waterSplashSound.load(descriptor, 1);
            AssetFileDescriptor descriptor2 = assetManager.openFd("game_over.wav");
            gameOverID = gameOverSound.load(descriptor2, 1);
            AssetFileDescriptor descriptor3 = assetManager.openFd("win_game.mp3");
            winGameID = winGameSound.load(descriptor3, 1);
            AssetFileDescriptor descriptor4 = assetManager.openFd("bomb.wav");
            bombID = bombSound.load(descriptor4, 1);
        } catch(IOException e){
            e.printStackTrace();
        }

        int width = getScreenWidth() - 100;
        int height = getScreenHeight();
        enemy1X = (int) (Math.random() * width + 50);
        enemy2X = (int) (Math.random() * width + 50);
        enemy3X = (int) (Math.random() * width + 50);
        enemy4X = (int) (Math.random() * width + 50);
        waterBlastX = width/2 + 40;
        waterBlastY = height - 260;
        waterBlast2X = width/2 + 40;
        waterBlast2Y = height - 260;
        waterBlast3X = width/2 + 40;
        waterBlast3Y = height - 260;
        squidHeroX = width/2 - 55;
        squidHeroY = height - 210;
    }

    protected void onPause(){
        super.onPause();
        gameView.pause();
    }

    protected void onResume(){
        super.onResume();
        gameView.resume();
    }

    public class GameView extends SurfaceView implements Runnable{

        boolean threadOK = true;
        Thread ViewThread = null;
        SurfaceHolder holder;
        int enemy1Hit = 9, enemy2Hit = 9, enemy3Hit = 9, enemy4Hit = 9;
        boolean shot1 = false, shot2 = false, shot3 = false;
        int aliensDefeated = 0,aliensEscaped = 0;

        public GameView(Context context) {
            super(context);
            holder = this.getHolder();
        }

        @Override
        public void run() {
            while(threadOK){
                if (!holder.getSurface().isValid()){
                    continue;
                }

                Canvas gameCanvas = holder.lockCanvas();
                enemy1Rect = new Rect(enemy1X, enemy1Y, enemy1X + enemy1.getWidth(), enemy1Y + enemy1.getHeight());
                enemy2Rect = new Rect(enemy2X, enemy2Y, enemy2X + enemy2.getWidth(), enemy2Y + enemy2.getHeight());
                enemy3Rect = new Rect(enemy3X, enemy3Y, enemy3X + enemy3.getWidth(), enemy3Y + enemy3.getHeight());
                enemy4Rect = new Rect(enemy4X, enemy4Y, enemy4X + enemy4.getWidth(), enemy4Y + enemy4.getHeight());
                waterBlastRect = new Rect(waterBlastX, waterBlastY, waterBlastX + waterBlast.getWidth(), waterBlastY + waterBlast.getHeight());
                waterBlastRect2 = new Rect(waterBlast2X, waterBlast2Y, waterBlast2X + waterBlast2.getWidth(), waterBlast2Y + waterBlast2.getHeight());
                waterBlastRect3 = new Rect(waterBlast3X, waterBlast3Y, waterBlast3X + waterBlast3.getWidth(), waterBlast3Y + waterBlast3.getHeight());
                myDraw(gameCanvas);
                holder.unlockCanvasAndPost(gameCanvas);
            }
        }

        protected void myDraw(Canvas canvas){
            drawPaint.setAlpha(255);
            canvas.drawColor(backgroundColor);
            recPaint.setAlpha(255);

            textPaint.setAlpha(255);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(40);
            textPaint.setTypeface(Typeface.create("casual", Typeface.BOLD));
            canvas.drawText("ALIENS DEFEATED: " + aliensDefeated + "/50", 0, 50, textPaint);
            canvas.drawText("ALIENS ESCAPED: " + aliensEscaped + "/5", getWidth() - 400, 50, textPaint);

            canvas.drawBitmap(squidHero, squidHeroX, squidHeroY, drawPaint);
            canvas.drawBitmap(backgroundStar, backgroundStarX, backgroundStarY, drawPaint);
            canvas.drawBitmap(backgroundStar, backgroundStarX + 120, backgroundStarY + 1200, drawPaint);
            canvas.drawBitmap(backgroundStar, backgroundStarX + 800, backgroundStarY + 1100, drawPaint);
            canvas.drawBitmap(backgroundStar, backgroundStarX + 280, backgroundStarY + 800, drawPaint);
            canvas.drawBitmap(backgroundStar, backgroundStarX + 560, backgroundStarY + 500, drawPaint);
            canvas.drawBitmap(backgroundStar, backgroundStarX + 210, backgroundStarY + 250, drawPaint);
            canvas.drawBitmap(backgroundStar, backgroundStarX + 700, backgroundStarY + 100, drawPaint);
            canvas.drawBitmap(enemy1, enemy1X, enemy1Y, drawPaint);
            canvas.drawBitmap(enemy2, enemy2X, enemy2Y, drawPaint);
            canvas.drawBitmap(enemy3, enemy3X, enemy3Y, drawPaint);
            canvas.drawBitmap(enemy4, enemy4X, enemy4Y, drawPaint);
            canvas.drawBitmap(waterBlast, waterBlastX, waterBlastY, drawPaint);
            canvas.drawBitmap(waterBlast2, waterBlast2X, waterBlast2Y, drawPaint);
            canvas.drawBitmap(waterBlast3, waterBlast3X, waterBlast3Y, drawPaint);

            if (enemy1Hit < 8){
                canvas.drawBitmap(explosion, enemy1XOld, enemy1YOld, drawPaint);
                enemy1Hit++;
            }
            if (enemy2Hit < 8){
                canvas.drawBitmap(explosion, enemy2XOld, enemy2YOld, drawPaint);
                enemy2Hit++;
            }
            if (enemy3Hit < 8){
                canvas.drawBitmap(explosion, enemy3XOld, enemy3YOld, drawPaint);
                enemy3Hit++;
            }
            if (enemy4Hit < 8){
                canvas.drawBitmap(explosion, enemy4XOld, enemy4YOld, drawPaint);
                enemy4Hit++;
            }

            if (waterBlastX < 0 || waterBlastX > canvas.getWidth() || waterBlastY < 0 || waterBlastY > canvas.getHeight()){
                waterBlastX = getWidth()/2 - 10;
                waterBlastY = getHeight() - 260;
                waterBlastXVel = 0;
                waterBlastYVel = 0;
                shot1 = false;
            }
            if (waterBlast2X < 0 || waterBlast2X > canvas.getWidth() || waterBlast2Y < 0 || waterBlast2Y > canvas.getHeight()){
                waterBlast2X = getWidth()/2 - 10;
                waterBlast2Y = getHeight() - 260;
                waterBlast2XVel = 0;
                waterBlast2YVel = 0;
                shot2 = false;
            }
            if (waterBlast3X < 0 || waterBlast3X > canvas.getWidth() || waterBlast3Y < 0 || waterBlast3Y > canvas.getHeight()){
                waterBlast3X = getWidth()/2 - 10;
                waterBlast3Y = getHeight() - 260;
                waterBlast3XVel = 0;
                waterBlast3YVel = 0;
                shot3 = false;
            }

            if (enemy1Y > canvas.getHeight()){
                enemy1X = (int) (Math.random() * getWidth() - 50);
                enemy1Y = -200;
                aliensEscaped++;
            }
            if (enemy2Y > canvas.getHeight()){
                enemy2X = (int) (Math.random() * getWidth() - 50);
                enemy2Y = -200;
                aliensEscaped++;
            }
            if (enemy3Y > canvas.getHeight()){
                enemy3X = (int) (Math.random() * getWidth() - 50);
                enemy3Y = -200;
                aliensEscaped++;
            }
            if (enemy4Y > canvas.getHeight()){
                enemy4X = (int) (Math.random() * getWidth() - 50);
                enemy4Y = -200;
                aliensEscaped++;
            }

            if (aliensEscaped == 5){
                gameOverSound.play(gameOverID, 1, 1, 0, 0, 1);
                Intent intent = new Intent(getContext(), GameOver.class);
                getContext().startActivity(intent);
            }
            if (aliensDefeated == 50){
                winGameSound.play(gameOverID, 1, 1, 0, 0, 1);
                Intent intent = new Intent(getContext(), GameWin.class);
                getContext().startActivity(intent);
            }

            if (Rect.intersects(waterBlastRect, enemy1Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy1XOld = enemy1X;
                enemy1YOld = enemy1Y;
                enemy1X = (int) (Math.random() * getWidth() - 50);
                enemy1Y = -200;
                waterBlastX = getWidth()/2 - 10;
                waterBlastY = getHeight() - 260;
                waterBlastXVel = 0;
                waterBlastYVel = 0;
                enemy1Hit = 0;
                shot1 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect2, enemy1Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy1XOld = enemy1X;
                enemy1YOld = enemy1Y;
                enemy1X = (int) (Math.random() * getWidth() - 50);
                enemy1Y = -200;
                waterBlast2X = getWidth()/2 - 10;
                waterBlast2Y = getHeight() - 260;
                waterBlast2XVel = 0;
                waterBlast2YVel = 0;
                enemy1Hit = 0;
                shot2 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect3, enemy1Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy1XOld = enemy1X;
                enemy1YOld = enemy1Y;
                enemy1X = (int) (Math.random() * getWidth() - 50);
                enemy1Y = -200;
                waterBlast3X = getWidth()/2 - 10;
                waterBlast3Y = getHeight() - 260;
                waterBlast3XVel = 0;
                waterBlast3YVel = 0;
                enemy1Hit = 0;
                shot3 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect, enemy2Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy2XOld = enemy2X;
                enemy2YOld = enemy2Y;
                enemy2X = (int) (Math.random() * getWidth() - 50);
                enemy2Y = -200;
                waterBlastX = getWidth()/2 - 10;
                waterBlastY = getHeight() - 260;
                waterBlastXVel = 0;
                waterBlastYVel = 0;
                enemy2Hit = 0;
                shot1 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect2, enemy2Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy2XOld = enemy2X;
                enemy2YOld = enemy2Y;
                enemy2X = (int) (Math.random() * getWidth() - 50);
                enemy2Y = -200;
                waterBlast2X = getWidth()/2 - 10;
                waterBlast2Y = getHeight() - 260;
                waterBlast2XVel = 0;
                waterBlast2YVel = 0;
                enemy2Hit = 0;
                shot2 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect3, enemy2Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy2XOld = enemy2X;
                enemy2YOld = enemy2Y;
                enemy2X = (int) (Math.random() * getWidth() - 50);
                enemy2Y = -200;
                waterBlast3X = getWidth()/2 - 10;
                waterBlast3Y = getHeight() - 260;
                waterBlast3XVel = 0;
                waterBlast3YVel = 0;
                enemy2Hit = 0;
                shot3 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect, enemy3Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy3XOld = enemy3X;
                enemy3YOld = enemy3Y;
                enemy3X = (int) (Math.random() * getWidth() - 50);
                enemy3Y = -200;
                waterBlastX = getWidth()/2 - 10;
                waterBlastY = getHeight() - 260;
                waterBlastXVel = 0;
                waterBlastYVel = 0;
                enemy3Hit = 0;
                shot1 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect2, enemy3Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy3XOld = enemy3X;
                enemy3YOld = enemy3Y;
                enemy3X = (int) (Math.random() * getWidth() - 50);
                enemy3Y = -200;
                waterBlast2X = getWidth()/2 - 10;
                waterBlast2Y = getHeight() - 260;
                waterBlast2XVel = 0;
                waterBlast2YVel = 0;
                enemy3Hit = 0;
                shot2 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect3, enemy3Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy3XOld = enemy3X;
                enemy3YOld = enemy3Y;
                enemy3X = (int) (Math.random() * getWidth() - 50);
                enemy3Y = -200;
                waterBlast3X = getWidth()/2 - 10;
                waterBlast3Y = getHeight() - 260;
                waterBlast3XVel = 0;
                waterBlast3YVel = 0;
                enemy3Hit = 0;
                shot3 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect, enemy4Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy4XOld = enemy4X;
                enemy4YOld = enemy4Y;
                enemy4X = (int) (Math.random() * getWidth() - 50);
                enemy4Y = -200;
                waterBlastX = getWidth()/2 - 10;
                waterBlastY = getHeight() - 260;
                waterBlastXVel = 0;
                waterBlastYVel = 0;
                enemy4Hit = 0;
                shot1 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect2, enemy4Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy4XOld = enemy4X;
                enemy4YOld = enemy4Y;
                enemy4X = (int) (Math.random() * getWidth() - 50);
                enemy4Y = -200;
                waterBlast2X = getWidth()/2 - 10;
                waterBlast2Y = getHeight() - 260;
                waterBlast2XVel = 0;
                waterBlast2YVel = 0;
                enemy4Hit = 0;
                shot2 = false;
                aliensDefeated++;
            }
            if (Rect.intersects(waterBlastRect3, enemy4Rect)){
                bombSound.play(bombID, 1, 1, 0, 0, 1);
                enemy4XOld = enemy4X;
                enemy4YOld = enemy4Y;
                enemy4X = (int) (Math.random() * getWidth() - 50);
                enemy4Y = -200;
                waterBlast3X = getWidth()/2 - 10;
                waterBlast3Y = getHeight() - 260;
                waterBlast3XVel = 0;
                waterBlast3YVel = 0;
                enemy4Hit = 0;
                shot3 = false;
                aliensDefeated++;
            }

            enemy1Y += enemy1YSpeed;
            enemy2Y += enemy2YSpeed;
            enemy3Y += enemy3YSpeed;
            enemy4Y += enemy4YSpeed;
            waterBlastX += waterBlastXVel;
            waterBlastY += waterBlastYVel;
            waterBlast2X += waterBlast2XVel;
            waterBlast2Y += waterBlast2YVel;
            waterBlast3X += waterBlast3XVel;
            waterBlast3Y += waterBlast3YVel;
        }

        public boolean onTouchEvent(MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                waterBlastXEnd = (int) event.getX();
                waterBlastYEnd = (int) event.getY();

                if (!shot1) {
                    waterSplashSound.play(waterSplashID, 1, 1, 0, 0, 1);
                    double theta = Math.atan2(waterBlastYEnd - waterBlastY, waterBlastXEnd - waterBlastX);
                    waterBlastXVel = 13 * Math.cos(theta);
                    waterBlastYVel = 13 * Math.sin(theta);
                    shot1 = true;
                    return true;
                } else if (!shot2) {
                    waterSplashSound.play(waterSplashID, 1, 1, 0, 0, 1);
                    double theta = Math.atan2(waterBlastYEnd - waterBlast2Y, waterBlastXEnd - waterBlast2X);
                    waterBlast2XVel = 13 * Math.cos(theta);
                    waterBlast2YVel = 13 * Math.sin(theta);
                    shot2 = true;
                    return true;
                } else if (!shot3) {
                    waterSplashSound.play(waterSplashID, 1, 1, 0, 0, 1);
                    double theta = Math.atan2(waterBlastYEnd - waterBlast3Y, waterBlastXEnd - waterBlast3X);
                    waterBlast3XVel = 13 * Math.cos(theta);
                    waterBlast3YVel = 13 * Math.sin(theta);
                    shot3 = true;
                    return true;
                }
            }
            return true;
        }

        public void pause(){
            threadOK = false;
            while (true){
                try {
                   ViewThread.join();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
            ViewThread = null;
        }

        public void resume(){
            threadOK = true;
            ViewThread = new Thread(this);
            ViewThread.start();
        }
    }
}
