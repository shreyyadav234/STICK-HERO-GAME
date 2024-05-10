package com.example.learningjavafx;

import javafx.application.Platform;

import java.io.IOException;

import static javafx.application.Application.launch;

public class GameController implements Runnable {

    private static final int RESET_DELAY_MS = 2000;
    private static final int LOOP_DELAY_MS = 10;

    private HelloApplication game;

    public GameController(HelloApplication game) {
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            checkAndResetGame();

            sleep(LOOP_DELAY_MS);
        }
    }

    private void checkAndResetGame() {
        if (HelloApplication.LineMovementDone) {
            Platform.runLater(() -> {
                try {
                    resetGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            sleep(RESET_DELAY_MS);
        }
    }

    private void resetGame() throws IOException {
        game.Repeat(null);
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}