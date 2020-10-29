package com.example.breakoutmini_game;

import android.graphics.RectF;

public class Brick {

    private RectF rect;

    private boolean isVisible;

    private boolean isDifferent;

    //-----CONSTRUCTOR
    public Brick(int row, int column, int width, int height){

        isVisible = true;

        isDifferent = false;

        int padding = 1;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    //-----METHODS
    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public void setDifferent(){
        isDifferent = true;
    }

    public boolean getVisibility(){
        return isVisible;
    }

    public boolean getDifferent(){
        return isDifferent;
    }
}
