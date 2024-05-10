// PauseScreenController.java
package com.example.learningjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ScreenController {
    private HelloController gameController;
    public void setGameController(HelloController gameController) {
        this.gameController = gameController;
    }

    @FXML
    private void resumeGame(ActionEvent event) {

        if (gameController == null) {
        }else{
            gameController.resumeGame();
        }
    }

    @FXML
    private void exitGame(ActionEvent event) {

        Platform.exit();
        System.exit(0);
    }
}
