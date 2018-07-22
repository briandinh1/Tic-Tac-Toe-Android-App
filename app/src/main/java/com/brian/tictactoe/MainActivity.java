package com.brian.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // used to reinstantiate after orientation change
    private static final String AI_ACTIVE = "tictactoe_AI_active";
    private static final String SCORE_OPEN = "tictactoe_score_open";
    private static final String CURRENT_TURN = "tictactoe_current_turn";
    private static final String TURNS_LEFT = "tictactoe_turns_left";
    private static final String PLAYER1_POINTS = "tictactoe_player1_points";
    private static final String PLAYER2_POINTS = "tictactoe_player2_points";

    private static final int TURNS = 9; // only 9 turns possible
    private Button[][] mButtons;
    private boolean mAIActive;
    private boolean mScoreOpen; // used to determine if score is shown
    private boolean mPlayer1Turn;
    private int mTurnsLeft;
    private int mPlayer1Points;
    private int mPlayer2Points;
    private TextView mTextViewPlayer1Points;
    private TextView mTextViewPlayer2Points;
    private Button mButtonScore;
    private Button mButtonAI;
    private Button mButtonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtons = new Button[3][3];
        mAIActive = false;
        mScoreOpen = false;
        mPlayer1Turn = true; // always start with player1
        mTurnsLeft = TURNS;
        mPlayer1Points = 0;
        mPlayer2Points = 0;
        mTextViewPlayer1Points = findViewById(R.id.text_view_player1_score);
        mTextViewPlayer2Points = findViewById(R.id.text_view_player2_score);
        mButtonScore = findViewById(R.id.button_score);
        mButtonAI = findViewById(R.id.button_AI);
        mButtonReset = findViewById(R.id.button_reset);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
                mButtons[i][j] = findViewById(resourceID);
                mButtons[i][j].setOnClickListener(this);
            }
        }

        mButtonScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScore();
            }
        });

        mButtonAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAIActive)
                    Toast.makeText(MainActivity.this, "Player 2 Set", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "AI Mode On", Toast.LENGTH_SHORT).show();
                mAIActive = !mAIActive;
                resetBoard();
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBoard();
            }
        });
    }

    // general onclick for all 9 of the game buttons
    @Override
    public void onClick(View view) {
        // if button already has been played, don't do anything
        if (!((Button) view).getText().toString().equals("")) return;

        // game with a human player vs AI
        if (mAIActive) {
            ((Button)view).setText("X"); // human move
            --mTurnsLeft;
            if(isEndGame()) return;
            Button button = findViewById(findBestMove()); // AI's turn
            button.setText("O");
            --mTurnsLeft;
            if (isEndGame()) return;
        }
        else { // game with two human players
            if (mPlayer1Turn)
                ((Button)view).setText("X");
            else
                ((Button)view).setText("O");
            mPlayer1Turn = !mPlayer1Turn;
            --mTurnsLeft;
            if(isEndGame()) return;
        }
    }

    private void showScore() {
        if (!mScoreOpen) {
            mButtonAI.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mTextViewPlayer1Points.setVisibility(View.VISIBLE);
            mTextViewPlayer2Points.setVisibility(View.VISIBLE);
            mScoreOpen = true;
        }
        else {
            mButtonAI.setVisibility(View.VISIBLE);
            mButtonReset.setVisibility(View.VISIBLE);
            mTextViewPlayer1Points.setVisibility(View.INVISIBLE);
            mTextViewPlayer2Points.setVisibility(View.INVISIBLE);
            mScoreOpen = false;
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                mButtons[i][j].setText("");
        mTurnsLeft = TURNS;
    }

    // checks if X or O won, or there is a tie. return false if none
    private boolean isEndGame() {
        String result = evaluateBoard(new String[3][3], false); // not using array
        if (result.equals("X")) {
            player1Win();
            return true;
        }
        else if (result.equals("O")) {
            player2Win();
            return true;
        }
        else if (mTurnsLeft == 0) {
            tieGame();
            return true;
        }
        return false;
    }

    private String evaluateBoard(String[][] values, boolean minimax) {
        // since we have to do a super long function call to get the string,
        // its better just to make another matrix. easier readability too
        // if minimax = true is passed, can just use its own values array
        if (!minimax) { // otherwise this is created to hold the values of the buttons
            values = new String[3][3];
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    values[i][j] = mButtons[i][j].getText().toString();
        }

        // check rows
        for (int i = 0; i < 3; i++)
            if (!values[i][0].equals("")
                    && values[i][0].equals(values[i][1])
                    && values[i][1].equals(values[i][2])) return values[i][0];

        // check cols
        for (int i = 0; i < 3; i++)
            if (!values[0][i].equals("")
                    && values[0][i].equals(values[1][i])
                    && values[1][i].equals(values[2][i])) return values[0][i];

        // check top left -> bottom right diagonal
        if (!values[0][0].equals("")
                && values[0][0].equals(values[1][1])
                && values[1][1].equals(values[2][2])) return values[0][0];

        // check top right -> bottom left diagonal
        if (!values[0][2].equals("")
                && values[0][2].equals(values[1][1])
                && values[1][1].equals(values[2][0])) return values[0][2];

        // after checking every row, col, diagonals
        return "";
    }

    private void player1Win() {
        Toast.makeText(MainActivity.this, "Player 1 Wins", Toast.LENGTH_SHORT).show();
        ++mPlayer1Points;
        updatePoints();
        resetBoard();
    }

    private void player2Win() {
        Toast.makeText(MainActivity.this, "Player 2 Wins", Toast.LENGTH_SHORT).show();
        ++mPlayer2Points;
        updatePoints();
        resetBoard();
    }

    private void tieGame() {
        Toast.makeText(MainActivity.this, "Tie Game", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePoints() {
        mTextViewPlayer1Points.setText("Player 1: " + mPlayer1Points);
        mTextViewPlayer2Points.setText("Player 2: " + mPlayer2Points);
    }

    private int findBestMove() {
        int bestMove = -1; // will be the resource id of the best button to play
        int bestValue = Integer.MIN_VALUE;

        // since we have to do a super long function call to get the string,
        // its better just to make another matrix. easier readability too
        // also avoid playing with the button text while doing minimax
        String[][] values = new String[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                values[i][j] = mButtons[i][j].getText().toString();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3 ; j++) {
                if (values[i][j].equals("")){
                    values[i][j] = "O"; // only ever evaluate for AI player2
                    --mTurnsLeft;
                    int currentValue = minimax(values, false);
                    ++mTurnsLeft;
                    values[i][j] = "";
                    if (currentValue > bestValue) {
                        bestValue = currentValue;
                        String buttonID = "button_" + i + j;
                        bestMove = getResources().getIdentifier(buttonID, "id", getPackageName());
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax (String[][] values, boolean isMax) {
        String result = evaluateBoard(values, true);
        if (result.equals("X"))
            return -100;
        else if (result.equals("O"))
            return 100;
        else if (mTurnsLeft == 0)
            return 0;

        // try to help AI player2 win by maximizing score
        if (isMax) {
            int bestValue = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (values[i][j].equals("")) {
                        values[i][j] = "O";
                        --mTurnsLeft;
                        bestValue = Math.max(bestValue, minimax(values, !isMax));
                        ++mTurnsLeft;
                        values[i][j] = "";
                    }
                }
            }
            return bestValue;
        }
        else {
            int bestValue = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (values[i][j].equals("")) {
                        values[i][j] = "X";
                        --mTurnsLeft;
                        bestValue = Math.min(bestValue, minimax(values, !isMax));
                        ++mTurnsLeft;
                        values[i][j] = "";
                    }
                }
            }
            return bestValue;
        }
    }

    // handle orientation changes
    // X and O state is handled in xml file by freezesText=true
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AI_ACTIVE, mAIActive);
        outState.putBoolean(SCORE_OPEN, mScoreOpen);
        outState.putBoolean(CURRENT_TURN, mPlayer1Turn);
        outState.putInt(TURNS_LEFT, mTurnsLeft);
        outState.putInt(PLAYER1_POINTS, mPlayer1Points);
        outState.putInt(PLAYER2_POINTS, mPlayer2Points);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAIActive = savedInstanceState.getBoolean(AI_ACTIVE);
        mScoreOpen = !savedInstanceState.getBoolean(SCORE_OPEN);
        mPlayer1Turn = savedInstanceState.getBoolean(CURRENT_TURN);
        mTurnsLeft = savedInstanceState.getInt(TURNS_LEFT);
        mPlayer1Points = savedInstanceState.getInt(PLAYER1_POINTS);
        mPlayer2Points = savedInstanceState.getInt(PLAYER2_POINTS);
        updatePoints();
        showScore();
    }
}
