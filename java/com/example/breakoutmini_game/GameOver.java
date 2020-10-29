package com.example.breakoutmini_game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import java.util.Vector;

public class GameOver {

    private int screenX, screenY;
    public enum ButtonResult { Retry, Exit}
    Button retryButton;
    Button exitButton;

    public class Button{
        public Rect rect;
        public ButtonResult action;
    }

    private Vector<Button> _buttons;

    GameOver(){
        screenX = BreakoutGame.BreakoutView.screenX;
        screenY = BreakoutGame.BreakoutView.screenY;

        _buttons = new Vector<Button>();
        Paint paint;
        paint = new Paint();
        paint.setTextSize(100);

        String text = "RETRY";
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int height = (int) (paint.descent() - paint.ascent());

        retryButton = new Button();
        retryButton.rect = new Rect();
        retryButton.rect.left = (screenX * 1 / 4) - (bounds.width() / 2);
        retryButton.rect.top = (screenY * 3 / 4) - height / 2;
        retryButton.rect.right = retryButton.rect.left + bounds.width();
        retryButton.rect.bottom = retryButton.rect.top + height;
        retryButton.action = ButtonResult.Retry;

        text = "EXIT";
        paint.getTextBounds(text, 0, text.length(), bounds);
        exitButton = new Button();
        exitButton.rect = new Rect();
        exitButton.rect.left = (screenX * 3 / 4) - (bounds.width() / 2);
        exitButton.rect.top = (screenY * 3 / 4) - height / 2;
        exitButton.rect.right = exitButton.rect.left + bounds.width();
        exitButton.rect.bottom = exitButton.rect.top + height;
        exitButton.action = ButtonResult.Exit;

        _buttons.addElement(retryButton);
        _buttons.addElement(exitButton);
    }

    public Vector<Button> getButtons(){
        return _buttons;
    }

    public void show(SurfaceHolder holder, Canvas canvas, Paint paint){
        if(holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 64, 64, 64));

            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(canvas.getHeight() / 8f);

            String text = "Game Over";
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            float height = paint.descent() - paint.ascent();
            float offset = (height / 2) - paint.descent();
            float xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            float yPos = (canvas.getHeight() / 2) + offset;
            canvas.drawText(text, xPos, yPos, paint);

            text = "RETRY";
            paint.setColor(Color.argb(255, 0, 204, 0));
            paint.setTextSize(100);

            canvas.drawText(text, retryButton.rect.left, retryButton.rect.bottom, paint);

            text = "EXIT";
            paint.setColor(Color.argb(255, 255, 0, 0));

            canvas.drawText(text, exitButton.rect.left, exitButton.rect.bottom, paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }
}
