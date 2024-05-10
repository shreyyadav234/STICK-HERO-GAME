package com.example.learningjavafx;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class HelloApplication extends Application {

    private Stage stage;
    private Scene scene;
    private AnchorPane root;
    @FXML

    private double x;

    private Line line1;
    private Timeline growthTimeline;
    private RotateTransition rotationTransition;
    private boolean isGrowing = false;
    private boolean isFirstTime = true;
    public static boolean LineMovementDone = false;


    private ImageView imageView;
    private Rectangle rectangle2;

    public static void main(String[] args) {
        launch();
    }

    private double generateRandomWidth() {

        Random random = new Random();
        return random.nextDouble(100, 280);
    }

    public void Repeat(ActionEvent e) throws IOException {
        initializeFXML(e);
        initializeHero();
        initializeShapes();
        initializeScene();
        initializeAnimations();
        initializeKeyHandlers();
        showStage();
    }

    private void initializeFXML(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningjavafx/Imageview.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    }

    private void initializeHero() {
        Image hero = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/learningjavafx/hero.png")));
        imageView = new ImageView(hero);
        imageView.setFitWidth(140);
        imageView.setFitHeight(150);
        imageView.setLayoutX(463);
        imageView.setLayoutY(450);
        root.getChildren().add(imageView);
    }

    private void initializeShapes() {
        initializeLine();
        initializeRectangle();
        initializeRandomRectangle();
    }

    private void initializeLine() {
        line1 = new Line();
        line1.setStartX(600);
        line1.setStartY(600);
        line1.setEndX(600);
        line1.setEndY(600);
        line1.setStrokeWidth(10);
        line1.setStroke(Color.DARKVIOLET);
        line1.autosize();
    }

    private void initializeRectangle() {
        Rectangle rectangle = new Rectangle();
        rectangle.setX(400);
        rectangle.setY(600);
        rectangle.setWidth(200);
        rectangle.setHeight(400);
        rectangle.setFill(Color.BLACK);
        rectangle.autosize();
        root.getChildren().add(rectangle);
    }

    private void initializeRandomRectangle() {
        rectangle2 = new Rectangle();
        rectangle2.setX(1000);
        rectangle2.setY(600);
        rectangle2.setWidth(generateRandomWidth());
        rectangle2.setHeight(400);
        rectangle2.setFill(Color.GREEN);
        rectangle2.autosize();
        root.getChildren().add(rectangle2);
    }

    private void initializeScene() {
        scene = new Scene(root);
    }

    private void initializeAnimations() {
        initializeGrowthTimeline();
        initializeRotationTransition();
    }

    private void initializeGrowthTimeline() {
        growthTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(line1.endYProperty(), line1.getEndY())),
                new KeyFrame(Duration.seconds(5000), new KeyValue(line1.endYProperty(), line1.getEndY() - 1000000))
        );
    }

    private void initializeRotationTransition() {
        rotationTransition = new RotateTransition(Duration.seconds(1), line1);
        rotationTransition.setAxis(Rotate.Z_AXIS);
    }

    private void initializeKeyHandlers() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W) {
                handleWKeyPressed();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.W) {
                handleWKeyReleased();
            }
        });

        line1.requestFocus();
    }

    private void handleWKeyPressed() {
        isGrowing = true;
        growthTimeline.setCycleCount(Animation.INDEFINITE);
        growthTimeline.play();
    }

    private void handleWKeyReleased() {
        isGrowing = false;
        growthTimeline.stop();
        growthTimeline.setCycleCount(1);
        double pivotX = line1.getStartX();
        double pivotY = line1.getStartY() + line1.getEndY();
        Rotate rotate = new Rotate(90, line1.getStartX(), line1.getStartY());
        line1.getTransforms().add(rotate);
        rotationTransition.setCycleCount(1);
        rotationTransition.setOnFinished(rotationEvent -> handleRotationFinished());
        rotationTransition.play();
    }

    private void handleRotationFinished() {
        double lineEndX = line1.getEndX();
        double lineEndY = line1.getEndY();

        LineMovementDone = true;

        double rotatedX = Math.cos(Math.toRadians(90)) * (lineEndX - line1.getStartX()) - Math.sin(Math.toRadians(90)) * (lineEndY - line1.getStartY()) + line1.getStartX();

        double imageViewX = rotatedX - imageView.getFitWidth();
        double layoutXDiff = imageViewX - imageView.getLayoutX();

        TranslateTransition translate = new TranslateTransition(Duration.seconds(1), imageView);
        translate.setToX(imageView.getTranslateX() + layoutXDiff + 100);
        translate.setInterpolator(Interpolator.EASE_BOTH);
        translate.play();

        translate.setOnFinished(translateEvent -> handleTranslateFinished());
    }

    private void handleTranslateFinished() {
        if (imageView.getBoundsInParent().intersects(rectangle2.getBoundsInParent())) {

        } else {

        }

    }

    private void showStage() {
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/learningjavafx/StartScreen.fxml"));
        Parent root =loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}