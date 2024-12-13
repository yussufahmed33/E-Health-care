package master;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class GameController {
    private static final int CODE_LENGTH = 4;
    private static final int MAX_ATTEMPTS = 10;
    private List<Color> colors = Arrays.asList(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE, Color.ORANGE
    );
    private List<Color> secretCode;
    private int attempts;
    private Color[] guesses = new Color[CODE_LENGTH];
    private VBox mainLayout;
    private GridPane guessGrid;
    private Label feedbackLabel;
    private int seconds = 0;
    private int minutes = 0;
    private Timeline timer;
    private Label timerLabel;
    private Label resultLabel;

    public GameController() {
        attempts = 0;
        generateSecretCode();
    }

    public VBox createGameLayout() {
        mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        timerLabel = new Label("00:00");
        timerLabel.setId("timerLabel");
        resultLabel = new Label();
        resultLabel.setFont(new javafx.scene.text.Font("Arial", 16));
        resultLabel.setTextFill(Color.WHITE);
        Label titleLabel = new Label("Mastermind game!");
        titleLabel.setFont(new javafx.scene.text.Font("Arial", 24));
        titleLabel.setTextFill(Color.WHITE);
        Label instructionsLabel = new Label("Guess the  code by choosing 4 colors.\nYou have 10 chances.");
        instructionsLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        instructionsLabel.setTextFill(Color.WHITE);
        guessGrid = new GridPane();
        guessGrid.setHgap(15);
        guessGrid.setVgap(15);
        guessGrid.setAlignment(Pos.CENTER);
        for (int i = 0; i < CODE_LENGTH; i++) {
            Button colorButton = new Button("Pick your Color");
            colorButton.setPrefSize(120, 50);
            colorButton.setId("colorButton");
            int index = i;
            colorButton.setOnAction(e -> pickColor(colorButton, index));
            guessGrid.add(colorButton, i, 0);
        }
        Button submitButton = new Button("Submit Guess");
        submitButton.setId("submitButton");
        submitButton.setOnAction(e -> submitGuess());
        feedbackLabel = new Label();
        feedbackLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        feedbackLabel.setTextFill(Color.WHITE);
        HBox timerBox = new HBox(10);
        timerBox.setAlignment(Pos.TOP_LEFT);
        timerBox.getChildren().add(timerLabel);

        mainLayout.getChildren().addAll(titleLabel, instructionsLabel, timerBox, guessGrid, submitButton, feedbackLabel, resultLabel);
        return mainLayout;
    }
    public void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }
            updateTimerLabel();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    private void updateTimerLabel() {
        String time = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(time);
    }
    private void generateSecretCode() {
        secretCode = new ArrayList<>(colors);
        Collections.shuffle(secretCode);
        secretCode = secretCode.subList(0, CODE_LENGTH);
    }
    private void pickColor(Button button, int index) {
        Color chosenColor = chooseRandomColor();
        button.setStyle("-fx-background-color: " + toHexString(chosenColor) + ";");
        guesses[index] = chosenColor;
    }
    private Color chooseRandomColor() {
        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }
    private String toHexString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
    private void submitGuess() {
        if (attempts < MAX_ATTEMPTS) {
            boolean allGuessesFilled = true;
            for (Color guess : guesses) {
                if (guess == null) {
                    allGuessesFilled = false;
                    break;
                }
            }
            if (!allGuessesFilled) {
                feedbackLabel.setText("فكر صح !!!");
                feedbackLabel.setTextFill(Color.CORAL);
                return;
            }
            String feedback = giveFeedback(guesses);
            feedbackLabel.setText(feedback);
            attempts++;
            if (isCorrectGuess(guesses)) {
                feedbackLabel.setText("🎉يا ابن اللعيبة🎉");
                feedbackLabel.setTextFill(Color.GREEN);
                endGame(true);
            } else if (attempts == MAX_ATTEMPTS) {
                feedbackLabel.setText("Game Over! 😔 The secret code was: " + secretCode);
                feedbackLabel.setTextFill(Color.ALICEBLUE);
                endGame(false);
            }
        }
    }
    private void endGame(boolean isWin) {
        timer.stop();
        if (isWin) {
            resultLabel.setText("You won in " + String.format("%02d:%02d", minutes, seconds));
            resultLabel.setTextFill(Color.GREEN);
        } else {
            resultLabel.setText("You lost after " + String.format("%02d:%02d", minutes, seconds));
            resultLabel.setTextFill(Color.RED);
        }
    }
    private String giveFeedback(Color[] guessColors) {
        int correctPosition = 0;
        int correctColor = 0;
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (guessColors[i] != null && guessColors[i].equals(secretCode.get(i))) {
                correctPosition++;
            } else if (guessColors[i] != null && secretCode.contains(guessColors[i])) {
                correctColor++;
            }
        }

        return "Correct position: " + correctPosition + ", Correct color but wrong position: " + correctColor;
    }
    private boolean isCorrectGuess(Color[] guessColors) {
        return Arrays.equals(guessColors, secretCode.toArray());
    }
}