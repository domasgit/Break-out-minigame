package com.example.breakoutmini_game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Hud {

    private int currentScore;
    private int currentLevel;
    private int currentLives;
    private int screenX, screenY;

    Hud() {

        screenX = BreakoutGame.BreakoutView.screenX;
        screenY = BreakoutGame.BreakoutView.screenY;

    }

    public void draw(Canvas canvas, Paint paint) {

        currentScore = BreakoutGame.BreakoutView.score;
        currentLevel = BreakoutGame.BreakoutView.level;
        currentLives = BreakoutGame.BreakoutView.lives;
        paint.setColor(Color.argb(255, 255, 128, 0));
        paint.setTextSize(40);
        String text = "Score: " + currentScore;
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        canvas.drawText("Score: " + currentScore, 10, bounds.height() + 3, paint);

        paint.setTextSize(50);
        text = "Level: " + currentLevel;
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, (screenX / 2) - (bounds.width() / 2), bounds.height() + 3, paint);

        paint.setTextSize(40);
        text = "Lives: " + currentLives;
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, screenX - bounds.width() - 15, bounds.height() + 3, paint);
    }

}
