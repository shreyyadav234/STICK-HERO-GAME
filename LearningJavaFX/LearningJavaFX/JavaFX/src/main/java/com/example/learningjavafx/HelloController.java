package com.example.learningjavafx;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class HelloController extends Application implements Runnable {

    @FXML
    private Button playbutton;
    @FXML
    private Label scoreLabel;
    private Stage stage;
    private Scene scene;
    private AnchorPane root;
    private Line line1;
    private Timeline growthTimeline;

    private ScheduledExecutorService gameLoopExecutor; // Rename to gameLoopExecutor

    private RotateTransition rotationTransition;
    private boolean isGrowing = false;
    private boolean isFirstTime = true;
    private boolean heroMovedOrFallen = false;

    private ImageView imageView;
    private boolean isFirstSpacePress = true;

    private Rectangle rectangle2;
    private Rectangle rectangle;

    private FadeTransition fadeOutTransition;
    private FadeTransition fadeInTransition;

    private int successfulMovements;

    private Stage pauseStage;
    private Scene pauseScene;

    private boolean secondPress = false;
    public static boolean LineMovementDone = false;

    public static void main(String[] args) {
        launch();
    }

    public void usingFunctionality(String FuncName){
        if(FuncName == "spawnRectangle"){
            spawnRectangle();
        }else if (FuncName == "spawnHero"){
            spawnHero();
        }else if(FuncName == "initializeAnimations"){
            initializeAnimations();
        } else if (FuncName == "initializeScoreLabel") {
            initializeScoreLabel();
        }
    }

    public double randomGenerator(int value_start, int value_end){
        Random random = new Random();
        return random.nextDouble(value_start, value_end);
    }

    private double generateRandomWidth() {
        // Generate a random width between 100 and 280
        return randomGenerator(85, 280);
    }

    public void spawnRectangle() {
        rectangle2 = new Rectangle();
        rectangle2.setX(randomGenerator(650, 1300));
        rectangle2.setY(600);
        rectangle2.setWidth(generateRandomWidth());
        rectangle2.setHeight(400);
        rectangle2.setFill(Color.GREEN);
        rectangle2.autosize();

        root.getChildren().add(rectangle2);
    }

    public void spawnHero() {
        Image hero = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/learningjavafx/hero.png")));
        imageView = new ImageView(hero);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setLayoutX(463);
        imageView.setLayoutY(500);
        imageView.setId("Hero");
        root.getChildren().add(imageView);
    }

    public void initializeGame() {
        usingFunctionality("spawnHero");
        usingFunctionality("spawnRectangle");
        usingFunctionality("initializeAnimations");
        usingFunctionality("initializeScoreLabel");
    }

    public void initializeAnimations() {
        initializeFadeTransitions();
        initializeTimelineAndRotationTransition();
    }

    private void initializeFadeTransitions() {
        fadeOutTransition = new FadeTransition(Duration.seconds(1), imageView);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);

        fadeInTransition = new FadeTransition(Duration.seconds(1), imageView);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(1.0);

        fadeOutTransition.setOnFinished(fadeInEvent -> {
            // After the fade in, reset the game state or perform other actions
            try {
                resetGameState();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initializeTimelineAndRotationTransition() {
        growthTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(line1.endYProperty(), line1.getEndY())),
                new KeyFrame(Duration.seconds(5000), new KeyValue(line1.endYProperty(), line1.getEndY() - 1000000))
        );

        rotationTransition = new RotateTransition(Duration.seconds(1), line1);
        rotationTransition.setAxis(Rotate.Z_AXIS);

        rotationTransition.setOnFinished(rotationEvent -> handleRotationFinished());

        growthTimeline.setOnFinished(growthEvent -> handleGrowthFinished());
    }

    public void initializeScoreLabel() {

        scoreLabel = new Label("Score: " + successfulMovements);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(new Font(26));


        root.getChildren().add(scoreLabel);
        AnchorPane.setTopAnchor(scoreLabel, 10.0);
        AnchorPane.setRightAnchor(scoreLabel, 10.0);
    }


    public void resetGrowthTimeline() {
        growthTimeline.pause();
        growthTimeline.setCycleCount(1);
        growthTimeline.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO, new KeyValue(line1.endYProperty(), line1.getEndY())),
                new KeyFrame(Duration.seconds(5000), new KeyValue(line1.endYProperty(), line1.getEndY() - 1000000))
        );
    }

    public void handleGrowthFinished() {
        isGrowing = false;
        resetGrowthTimeline();
        growthTimeline.pause();
        growthTimeline.setCycleCount(1);

        double pivotX = line1.getStartX();
        double pivotY = line1.getStartY() + line1.getEndY();
        Rotate rotate = new Rotate(90, 600, 600);
        line1.getTransforms().add(rotate);

        rotationTransition.setCycleCount(1);
        rotationTransition.play();
    }

    public void handleRotationFinished() {
        double lineEndX = line1.getEndX();
        double lineEndY = line1.getEndY();

        LineMovementDone = true;

        double rotatedX = Math.cos(Math.toRadians(90)) * (lineEndX - line1.getStartX()) - Math.sin(Math.toRadians(90)) * (lineEndY - line1.getStartY()) + line1.getStartX();
        double rotatedY = Math.sin(Math.toRadians(90)) * (lineEndX - line1.getStartX()) + Math.cos(Math.toRadians(90)) * (lineEndY - line1.getStartY()) + line1.getStartY();

        double imageViewX = rotatedX - imageView.getFitWidth();
        double imageViewY = rotatedY - imageView.getFitHeight();

        double layoutXDiff = imageViewX - imageView.getLayoutX();

        TranslateTransition translate = new TranslateTransition(Duration.seconds(1), imageView);
        translate.setToX(imageView.getTranslateX() + layoutXDiff + 100);
        translate.setInterpolator(Interpolator.EASE_BOTH);
        translate.setOnFinished(translateEvent -> {
            try {
                handleTranslateFinished();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        translate.play();

    }

    public void handleTranslateFinished() throws IOException {


        List<Node> cherriesToRemove = root.getChildren().stream()
                .filter(node -> node instanceof ImageView && node.getId() != null && node.getId().equals("Cherry"))
                .filter(cherry -> imageView.getBoundsInParent().intersects(cherry.getBoundsInParent()))
                .collect(Collectors.toList());

        if (!cherriesToRemove.isEmpty()) {

            root.getChildren().removeAll(cherriesToRemove);
            System.out.println("Cherry eaten!");
        }

        if (imageView.getBoundsInParent().intersects(rectangle2.getBoundsInParent())) {
            line1.setVisible(false);
            fadeOutTransition.play();
            Rotate fallRotate = new Rotate(90, 600, 600);
            line1.getTransforms().add(fallRotate);
            heroMovedOrFallen = true;
        } else {

            TranslateTransition fallTransition = new TranslateTransition(Duration.seconds(1), imageView);
            fallTransition.setToY(imageView.getTranslateY() + 500);
            fallTransition.setInterpolator(Interpolator.EASE_BOTH);
            fallTransition.setOnFinished(fallEvent -> {
                try {
                    handleFallFinished();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Rotate fall = new Rotate(90, 600, 600);
            line1.getTransforms().add(fall);
            fallTransition.play();

            fallTransition.setOnFinished(event -> {
                this.stage.close();
            });
        }
    }

    public void handleFallFinished() throws IOException {

        heroMovedOrFallen = true;
    }

    public void resetGameState() throws IOException {

        for (Node node : root.getChildren()) {
            if (node instanceof ImageView && node.getId() != null && node.getId().equals("Hero")) {
                imageView = (ImageView) node;


                imageView.setLayoutX(imageView.getLayoutX() - 600);
                imageView.setLayoutY(450);

                break;
            }
        }

        fadeInTransition.play();
        fadeInTransition.setOnFinished(event -> {
            this.stage.close();
        });

        for (Node node : root.getChildren()) {
            if (node instanceof Rectangle && node.getId() != null && node.getId().equals("rectangle2")) {
                rectangle2 = (Rectangle) node;
                break;
            }
        }
        rectangle2.setWidth(generateRandomWidth());
    }


    @FXML
    public void startGame(ActionEvent e) throws IOException {
        Repeat();
    }
    private void spawnCherries() {
        for (int i = 0; i < 5; i++) {
            ImageView cherry = new ImageView(new Image(getClass().getResourceAsStream("/com/example/learningjavafx/cherry.png")));
            cherry.setFitWidth(50);
            cherry.setFitHeight(50);
            cherry.setId("Cherry");
            cherry.setLayoutX(600 + i * 100);
            cherry.setLayoutY(625);
            root.getChildren().add(cherry);
        }
    }
    private boolean isFlipped = false;

    public void Repeat() throws IOException {
        CompletableFuture<Void> repeatFuture = new CompletableFuture<>();

        LineMovementDone = false;

        CompletableFuture.runAsync(() -> {
            initializeRoot();
            initializeLineAndRectangle();
            initializeSceneAndStage();

            setEventHandlers();

            line1.requestFocus();
            this.stage.showAndWait();


            repeatFuture.complete(null);
        }, Platform::runLater);


        repeatFuture.thenRunAsync(() -> {
            try {
                Repeat();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, Platform::runLater);
    }

    private void initializeRoot() {

        if (this.root != null) {
            root.getChildren().clear();
        }


        root = new AnchorPane();
        root.setPrefSize(1920, 1080);

        Image backgroundImage = new Image(getClass().getResourceAsStream("/com/example/learningjavafx/background2.jpg"));
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        root.setBackground(new Background(background));
    }

    private void initializeLineAndRectangle() {
        // Create a new Line
        line1 = new Line();
        line1.setStartX(600);
        line1.setStartY(600);
        line1.setEndX(600);
        line1.setEndY(600);
        line1.setStrokeWidth(10);
        line1.setStroke(Color.RED);
        line1.autosize();


        rectangle = new Rectangle();
        rectangle.setX(400);
        rectangle.setY(600);
        rectangle.setWidth(200);
        rectangle.setHeight(400);
        rectangle.setFill(Color.BLACK);
        rectangle.autosize();


        root.getChildren().addAll(line1, rectangle);
    }

    private void initializeSceneAndStage() {
        initializeGame();


        Scene newScene = new Scene(root);
        this.stage = new Stage();
        this.stage.setScene(newScene);
    }

    private void setEventHandlers() {
        Scene newScene = this.stage.getScene();

        newScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                handleSpaceKeyPressed();
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                showPauseScreen();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                handleDownArrowKeyPressed();
            }
        });

        newScene.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                handleSpaceKeyReleased();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                handleDownArrowKeyReleased();
            }
        });
    }

    private void handleSpaceKeyPressed() {
        if (!isGrowing) {
            isGrowing = true;
            resetGrowthTimeline();
            growthTimeline.setCycleCount(Animation.INDEFINITE);
            growthTimeline.play();
        }
    }

    private void handleSpaceKeyReleased() {
        isGrowing = false;
        growthTimeline.pause();
        resetGrowthTimeline();
        handleGrowthFinished();
    }

    private void handleDownArrowKeyPressed() {
        flipHeroUpsideDownIfNeeded();
        setHeroUpsideDownScale();
    }

    private void handleDownArrowKeyReleased() {
        resetHeroUpsideDownIfNeeded();
        resetHeroScale();
    }

    private void flipHeroUpsideDownIfNeeded() {
        if (!isFlipped) {
            isFlipped = true;
            imageView.setLayoutY(imageView.getLayoutY() + imageView.getFitHeight());
        }
    }

    private void setHeroUpsideDownScale() {
        imageView.setScaleY(-1);
    }

    private void resetHeroUpsideDownIfNeeded() {
        if (isFlipped) {
            isFlipped = false;
            imageView.setLayoutY(imageView.getLayoutY() - imageView.getFitHeight());
        }
    }

    private void resetHeroScale() {
        imageView.setScaleY(1);
    }

    private void showPauseScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningjavafx/StopScreen.fxml"));
            AnchorPane pauseRoot = loader.load();

            ScreenController screenController = loader.getController();
            screenController.setGameController(this);

            initializePauseStage(pauseRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePauseStage(AnchorPane pauseRoot) {
        pauseScene = new Scene(pauseRoot);
        pauseStage = new Stage();
        pauseStage.setScene(pauseScene);
        pauseStage.showAndWait();
    }

    public void resumeGame() {
        pauseStage.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningjavafx/StartScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    @Override
    public void run() {
        try {
            Repeat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
