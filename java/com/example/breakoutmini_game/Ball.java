package com.example.breakoutmini_game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.Random;

import static com.example.breakoutmini_game.BreakoutGame.BreakoutView.paddle;

public class Ball extends VisibleObject{

    //-----DECLARATIONS
    private float _velocity, initialVelocity;
    private float _angle;
    private int screenX, screenY;
    private long elapsedTimeSinceStart;
    private final long minCollisionInterval;
    private long collisionTime;

    //-----CONSTRUCTOR
    public Ball(){

        setSize(10, 10);
        initialVelocity = 300;
        _velocity = initialVelocity;

        Random generator = new Random();
        _angle = generator.nextInt(360);
        if(_angle >= 70.0f && _angle <= 110.0f){
            _angle += 40;
        }
        if(_angle >= 250.0f && _angle <= 290.0f){
            _angle -= 40;
        }
        screenX = BreakoutGame.BreakoutView.screenX;
        screenY = BreakoutGame.BreakoutView.screenY;
        elapsedTimeSinceStart = 0;
        minCollisionInterval = 200;
        collisionTime = 0;

    }

    //-----METHODS

    //--UPDATE
    @Override
    public void update(long fps, long elapsedTime){

        elapsedTimeSinceStart += elapsedTime;
        if(elapsedTimeSinceStart < 3000)
            return;

        float xVelocity = _velocity * linearVelocityX(_angle);
        float yVelocity = _velocity * linearVelocityY(_angle);


        RectF rect = getBoundingRect();

        float left = rect.left + (xVelocity / fps);
        float top = rect.top + (yVelocity / fps);
        float right = rect.right + (xVelocity / fps);
        float bottom = rect.bottom + (yVelocity / fps);

        // collision with right and left wall
        if(left <= 0 || right >= screenX - 10){
            _angle = 360.0f - _angle;
            xVelocity = -xVelocity;

            if(_angle >= 70.0f && _angle <= 110.0f){
                _angle += 40;
            }
            if(_angle >= 250.0f && _angle <= 290.0f){
                _angle -= 40;
            }

            BreakoutGame.BreakoutView.soundPool.play(BreakoutGame.BreakoutView.ballID, 1, 1, 0, 0, 1);

        }

        // collision with top
        if(top <= 0){
            yVelocity = -yVelocity;
            _angle = 180.0f - _angle;

            if(_angle < 0.0f){
                _angle += 360.0f;
            }
            if(_angle > 360.0f){
                _angle = _angle - 360.0f;
            }

            BreakoutGame.BreakoutView.soundPool.play(BreakoutGame.BreakoutView.ballID, 1, 1, 0, 0, 1);
            //clearObstacleY(12);
        }

        // collision with bottom
        if(bottom >= screenY){
            BreakoutGame.BreakoutView.lives--;
            BreakoutGame.BreakoutView.soundPool.play(BreakoutGame.BreakoutView.loseLifeID, 1, 1, 0, 0, 1);

            if(BreakoutGame.BreakoutView.lives == 0){
                return;
            }

            reset();

        }

        // collision with bricks
        int numBricks = BreakoutGame.BreakoutView.numBricks;
        Brick[] bricks = BreakoutGame.BreakoutView.bricks;

        for(int i=0; i<numBricks; i++){
            if(bricks[i].getVisibility()){
                if(RectF.intersects(bricks[i].getRect(), getBoundingRect())){
                    bricks[i].setInvisible();
                    yVelocity = -yVelocity;

                    _angle = 180.0f - _angle;

                    if(_angle < 0.0f){
                        _angle += 360.0f;
                    }
                    if(_angle > 360.0f){
                        _angle = _angle - 360.0f;
                    }

                    BreakoutGame.BreakoutView.score = 50 + BreakoutGame.BreakoutView.score;

                    if(bricks[i].getDifferent()){
                        BreakoutGame.BreakoutView.paddle.increaseWidth(20);
                    }

                    if(BreakoutGame.BreakoutView.score * 50 == numBricks){
                        return;
                    }
                    BreakoutGame.BreakoutView.soundPool.play(BreakoutGame.BreakoutView.brickID, 1, 1, 0, 0, 1);
                }
            }
        }

        //collision with paddle
        Paddle paddle = BreakoutGame.BreakoutView.paddle;
        collisionTime += elapsedTime;

        if(paddle != null){
            RectF paddleRect = paddle.getBoundingRect();
            if(RectF.intersects(paddleRect, getBoundingRect()) &&
                    collisionTime > minCollisionInterval){
                yVelocity = -yVelocity;

                if(paddleRect.top + paddle.getHeight()/2 < getBoundingRect().bottom){
                    // collision at edge
                    _angle = 360.0f - _angle;
                }
                else {
                    // collision at top
                    _angle = 180.0f - _angle;
                }

                if(_angle < 0.0f){
                    _angle += 360.0f;
                }
                if(_angle >= 360.0f){
                    _angle = _angle - 360.0f;
                }
                clearObstacleY(paddle.getBoundingRect().top - 2);

                // adding spin to ball
                if(paddle.getMovementState() == Paddle.MovementState.Left){
                    Log.d("Angle is ", "" + _angle);

                    if(_angle > 45  && _angle < 270)
                    _angle -= 30.0f;

                    if(_angle < 0.0f){
                        _angle += 360.0f;
                    }
                }

                else if(paddle.getMovementState() == Paddle.MovementState.Right){
                    Log.d("Angle is ", "" + _angle);
                    if(_angle > 90 && _angle < 315) {
                        _angle += 30.0f;

                        if (_angle > 360.0f) {
                            _angle = _angle - 360.0f;
                        }
                    }
                }
                BreakoutGame.BreakoutView.soundPool.play(BreakoutGame.BreakoutView.ballID, 1, 1, 0, 0, 1);
                _velocity += 20.0f;
                collisionTime = 0;
            }
        }

        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + getWidth();
        rect.bottom = rect.top + getHeight();

        setPosition(rect.left, rect.top);

    }//END OF update()

    private float linearVelocityX(float angle){
        angle -= 90;
        if(angle < 0){
            angle += 360;
        }
        return (float)Math.cos(angle * 3.14159 / 180.0);
    }

    private float linearVelocityY(float angle){
        angle -= 90;
        if(angle < 0){
            angle += 360;
        }
        return (float)Math.sin(angle * 3.14159 / 180.0);
    }

    public void reset(){
        super.reset();
        _velocity = initialVelocity;
        elapsedTimeSinceStart = 0;

        Random generator = new Random();
        _angle = generator.nextInt(360);
        if(_angle >= 70.0f && _angle <= 110.0f){
            _angle += 40;
        }
        if(_angle >= 250.0f && _angle <= 290.0f){
            _angle -= 40;
        }
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.argb(255, 255, 255, 255));
        super.draw(canvas, paint);
    }

    public void clearObstacleY(float y){
        RectF rect = getBoundingRect();
        rect.bottom = y;
        rect.top = y - getHeight();
        setBoundingRect(rect);
    }

    public void clearObstacleX(float x){
        RectF rect = getBoundingRect();
        rect.left = x;
        rect.right = x + getWidth();
        setBoundingRect(rect);
    }

}
