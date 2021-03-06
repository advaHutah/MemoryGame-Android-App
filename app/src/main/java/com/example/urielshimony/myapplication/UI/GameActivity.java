package com.example.urielshimony.myapplication.UI;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.urielshimony.myapplication.R;
import com.example.urielshimony.myapplication.logic.GameManager;
import com.example.urielshimony.myapplication.logic.MemoryCard;

public class GameActivity extends AppCompatActivity {

    static GameManager gameManager;
    private GridLayout gameGrid;
    private Button[][] buttons;
    private String currentFlip;
    private int timeToStop;
    private int timeLeft;
    private String gameResult;

    final static int SECOND = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init
        super.onCreate(savedInstanceState);
        setTitle("Let's Play!");
        setContentView(R.layout.activity_game);
        this.gameGrid = (GridLayout) findViewById(R.id.grid);
        this.currentFlip = "First";
        this.timeToStop = gameManager.getSeconds();
        startTimer(this.timeToStop);
        setName(gameManager.getName());
        setLevel(gameManager.getDifficultLvl());
        setNewGrid(gameManager.getCardBoard().getRows(), gameManager.getCardBoard().getCols());


    }

    //create new grid according to spesific rows and cols
    private void setNewGrid(int rows, int cols) {
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        final int screenWidth = size.x;
        final int screenHeight = size.y;
        int buttonsWidth = (int) (screenWidth * 0.8 / cols);
        int buttonsHeight = (int) (screenHeight * 0.6 / rows);

        Log.d("width", "" + buttonsWidth);
        Log.d("H", "" + buttonsHeight);

        gameGrid.removeAllViews();
        gameGrid.setColumnCount(cols);
        gameGrid.setRowCount(rows);
        MemoryCard[][] cards = gameManager.getCards();

        this.buttons = new Button[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.buttons[i][j] = new Button(this);
                this.buttons[i][j].setBackgroundResource(cards[i][j].getBackImage());
                this.buttons[i][j].setLayoutParams(new LinearLayout.LayoutParams(buttonsWidth, buttonsHeight));
                this.buttons[i][j].setTag(cards[i][j]);
                this.buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setBackgroundResource(((MemoryCard) view.getTag()).getImage());
                        view.setEnabled(false);
                        gameManager.flipCard(((MemoryCard) view.getTag()).getCardId(), currentFlip);
                        if (currentFlip.equals("Second")) {
                            setEnableAll(false);
                            Handler hendler = new Handler();
                            hendler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateGrid();
                                    setEnableAll(true);

                                }
                            }, SECOND);
                        }
                        changeCurrentFlip();
                    }
                });

                gameGrid.addView(buttons[i][j]);
            }

        }
    }

    //set if it first or second card
    private void changeCurrentFlip() {
        if (this.currentFlip.equals("First")) this.currentFlip = "Second";
        else {
            this.currentFlip = "First";
        }
    }

    //update grid after flip
    private void updateGrid() {
        MemoryCard[][] cards = gameManager.getCards();
        int rows = cards.length;
        int cols = cards[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cards[i][j].getIsBeenMatched()) {
                    this.buttons[i][j].setBackgroundResource(cards[i][j].getImage());
                } else {
                    this.buttons[i][j].setBackgroundResource(cards[i][j].getBackImage());
                    this.buttons[i][j].setEnabled(true);
                }
            }
        }
        if (gameManager.isPlayerWon()) {
            gameManager.endGame();
            createEndOfGameActivity();
        }
    }


    public void setEnableAll(boolean enabledValue) {
        MemoryCard[][] cards = gameManager.getCards();
        int rows = cards.length;
        int cols = cards[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cards[i][j].getIsBeenMatched()) {

                } else {
                    this.buttons[i][j].setEnabled(enabledValue);
                }
            }
        }
    }

    private void startTimer(int timeToStop) {
        timeLeft = timeToStop;
        Handler hendler = new Handler();
        hendler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tick(timeLeft);
                if (timeLeft != 0) {
                    startTimer(--timeLeft);
                } else {
                    gameManager.endGame();
                    createEndOfGameActivity();
                }
            }
        }, SECOND);

    }

    private void tick(int seconds) {
        ((TextView) findViewById(R.id.Timer)).setText("" + seconds);

    }

    public void setName(String name) {
        ((TextView) findViewById(R.id.name)).setText("" + name);
    }

    public void createEndOfGameActivity() {
        this.gameResult = gameManager.getGameResult();
        Intent intent = new Intent(this, EndOfGameActivity.class);
        intent.putExtra("name", gameManager.getName());
        intent.putExtra("date_of_birth", gameManager.getDate());
        intent.putExtra("gameResult", gameResult);
        startActivity(intent);
    }

    public void setLevel(String level) {
        ((TextView) findViewById(R.id.dificultLevelLable)).setText("" + level);
    }
}