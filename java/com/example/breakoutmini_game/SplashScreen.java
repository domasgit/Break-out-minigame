package com.example.breakoutmini_game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class SplashScreen {

    public void show(SurfaceHolder holder, Canvas canvas, Paint paint){
        if(holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 64, 64, 64));

            paint.setColor(Color.argb(255, 0, 204, 0));
            paint.setTextSize(canvas.getHeight() / 5f);

            String text = "Breakout mini-game";
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            float height = paint.descent() - paint.ascent();
            float offset = (height / 2) - paint.descent();
            float xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            float yPos = (canvas.getHeight() / 2) + offset;
            canvas.drawText(text, xPos, yPos, paint);

            text = "Made by : Dominykas Jasiulionis";
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(canvas.getHeight() / 20);
            paint.getTextBounds(text, 0, text.length(), bounds);

            xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            yPos = (canvas.getHeight() * 3 / 4) + offset;
            canvas.drawText(text, xPos, yPos, paint);

            text = "Press screen to continue";
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(canvas.getHeight() / 25);
            paint.getTextBounds(text, 0, text.length(), bounds);

            xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            yPos = canvas.getHeight() - offset;
            canvas.drawText(text, xPos, yPos, paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }
}
