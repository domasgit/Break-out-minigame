package com.example.breakoutmini_game;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Iterator;

public class BreakoutGame extends Activity {

    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    private enum gameState{Playing, Paused, ShowingSplash, ShowingGameOver, Completed}

    public static class BreakoutView extends SurfaceView implements Runnable {

        //-----DECLARATIONS
        //--Game thread
        Thread gameThread = null;

        //--Game holder
        private static SurfaceHolder gameHolder;

        //--game state tracking
        private gameState _gameState;

        private static GameOver _gameOver;

        Rect menuButton;

        //--draw() objects
        private static Canvas canvas;
        private static Paint paint;

        //--for fps calculation and same game speeds on different devices
        long fps;
        private long timeThisFrame;

        //--for screen resolution calculations
        static int screenX;
        static int screenY;

        //--Objects
        static Paddle paddle;
        Ball ball;
        static Brick[] bricks = new Brick[200];
        Hud hud;
        static int numBricks = 0;

        // For sound FX
        static SoundPool soundPool;
        static int ballID = -1;
        static int brickID = -1;
        static int loseLifeID = -1;
        static int gameOverID = -1;
        static int winID = -1;


        static int score = 0;
        static int lives = 3;
        static int level = 1;

        //-----CONSTRUCTOR
        public BreakoutView(Context context) {
            super(context);

            gameHolder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            // Load the resolution into a Point object
            Point screenSize = new Point();
            display.getSize(screenSize);

            screenX = screenSize.x;
            screenY = screenSize.y;

            //Show starting screen by default
            _gameState = gameState.ShowingSplash;

            _gameOver = new GameOver();

            paddle = new Paddle();
            paddle.setInitialPosition(screenX / 2 - 65, screenY - 30);
            ball = new Ball();
            ball.setInitialPosition(screenX / 2, screenY / 2 + screenY / 15);
            hud = new Hud();

            menuButton = new Rect();

            // Load the sounds
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try{
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load sound fx in memory ready for use
                descriptor = assetManager.openFd("ball.wav");
                ballID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("brick.wav");
                brickID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("gameOver.wav");
                gameOverID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.wav");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("win.wav");
                winID = soundPool.load(descriptor, 0);

            }catch(IOException e){
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }


        }

        //-----MAIN METHODS

        @Override
        public void run() {
            while(!isPaused()){
                GameLoop();
            }
        }//----------END OF run()

            private boolean isPaused(){
                return _gameState == gameState.Paused;
            }

            private void GameLoop() {

                switch (_gameState) {

                    case ShowingSplash:
                        showSplash();
                        break;

                    case Playing:

                            // Capture the current time in milliseconds in startFrameTime
                            long startFrameTime = System.currentTimeMillis();

                            update();
                            draw();

                            // Calculate the fps this frame
                            timeThisFrame = System.currentTimeMillis() - startFrameTime;
                            if (timeThisFrame >= 1) {
                                fps = 1000 / timeThisFrame;
                            }
                        break;

                    case Completed:
                        showCompleted();
                        break;

                    case ShowingGameOver:
                        showGameOver();
                        break;

                    default:
                        break;





                }
            }//----------END OF GameLoop()


        public void update() {

            paddle.update(fps, timeThisFrame);
            ball.update(fps, timeThisFrame);

            if(lives == 0){
                _gameState = gameState.ShowingGameOver;
            }

        }//----------END OF update()

        public void draw() {

            // Make sure our drawing surface is valid
            if (gameHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = gameHolder.lockCanvas();

                // Draw the background
                canvas.drawColor(Color.argb(255,  64, 64, 64));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw the paddle
                paddle.draw(canvas, paint);

                // Draw the ball
                ball.draw(canvas, paint);

                // Draw the bricks
                // Change the brush color for drawing
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw the bricks if visible
                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {
                        if(bricks[i].getDifferent()){
                            paint.setColor(Color.argb(255,  204, 255, 255));
                            canvas.drawRect(bricks[i].getRect(), paint);
                            paint.setColor(Color.argb(255,  255, 255, 255));
                        }
                        else {
                            canvas.drawRect(bricks[i].getRect(), paint);
                        }
                    }
                }

                // Draw the HUD
                hud.draw(canvas, paint);

                // Has the player cleared the screen?
                if(score == numBricks * 50){
                    soundPool.play(winID, 1, 1, 0, 0, 1);
                    _gameState = gameState.Completed;
                }

                // Has the player lost?
                if(lives <= 0){
                    soundPool.play(gameOverID, 1, 1, 0, 0, 1);
                    _gameState = gameState.ShowingGameOver;
                }

                // Draw everything to the screen
                gameHolder.unlockCanvasAndPost(canvas);
            }

        }//----------END OF draw()

        //pause() & resume() methods
        public void pause() {
            _gameState = gameState.Paused;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        public void resume() {
            if(_gameState == gameState.Paused)
                _gameState = gameState.Playing;
            else
                _gameState = gameState.ShowingSplash;

            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:

                    //Player is in game screen
                    if(_gameState == gameState.Playing) {

                        if (motionEvent.getX() > screenX / 2) {
                            paddle.setMovementState(Paddle.MovementState.Right);
                        } else {
                            paddle.setMovementState(Paddle.MovementState.Left);
                        }
                    }

                    //Player is in splash screen
                    else if(_gameState == gameState.ShowingSplash){
                        _gameState = gameState.Playing;
                        restartGame();
                    }

                    // PLayer is in game over screen
                    else if(_gameState == gameState.ShowingGameOver){
                        float xPos = motionEvent.getX();
                        float yPos = motionEvent.getY();

                        Iterator itr = _gameOver.getButtons().iterator();
                        while(itr.hasNext()){
                            GameOver.Button button = (GameOver.Button)itr.next();
                            menuButton = button.rect;
                            if(xPos > menuButton.left && xPos < menuButton.right &&
                                    yPos > menuButton.top && yPos < menuButton.bottom){
                                switch (button.action){
                                    case Retry:
                                        _gameState = gameState.Playing;
                                        restartGame();
                                        break;
                                    case Exit:
                                        _gameState = gameState.ShowingSplash;
                                        break;
                                }
                            }
                        }
                    }

                    //Player is in level complete screen
                    else if(_gameState == gameState.Completed){
                        if(level <= 3) {
                            _gameState = gameState.Playing;
                            nextLevel();
                        }
                        else
                            _gameState = gameState.ShowingSplash;
                    }
                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:

                    paddle.setMovementState(Paddle.MovementState.Stopped);

                    break;
            }
            return true;
        }//----------END OF onTouchEvent

        //-----OTHER METHODS
        public void restartGame(){

            ball.reset();
            paddle.reset();

            drawBricks();

            level = 1;
            lives = 3;
            score = 0;

        }

        public void nextLevel(){

            ball.reset();
            paddle.reset();

            drawBricks();

            score = 0;
            lives = 3;

            //hud.setValues(score, lives);
            level++;
        }

        public void drawBricks(){

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            numBricks = 0;

            for(int column = 0; column < 8; column ++ ){
                for(int row = 0; row < 3; row ++ ){
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    if((row == 1) && (column == 2 || column == 5)){
                        bricks[numBricks].setDifferent();
                    }
                    numBricks ++;
                }
            }
        }

        public void showSplash(){
            SplashScreen splashScreen = new SplashScreen();
            splashScreen.show(gameHolder, canvas, paint);
        }

        public void showCompleted(){
            LevelCompleted levelCompleted = new LevelCompleted();
            levelCompleted.show(gameHolder, canvas, paint, level);
        }

        public void showGameOver(){
            GameOver gameOver = new GameOver();
            gameOver.show(gameHolder, canvas, paint);
        }

    }//----------END OF BreakoutView

    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        breakoutView.pause();
    }

}//----------END OF BreakoutGame Activity
