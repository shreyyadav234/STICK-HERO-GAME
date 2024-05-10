package com.example.learningjavafx;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Controller {
    @FXML
    private ImageView StickHero;

    public void initialize() {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(StickHero.xProperty(), StickHero.getX() + 50, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(StickHero.xProperty(), StickHero.getX() + 50, Interpolator.EASE_BOTH)),
                // Add more key frames as needed
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(StickHero.xProperty(), StickHero.getX() + 50, Interpolator.EASE_BOTH))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
