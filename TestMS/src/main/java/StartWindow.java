import com.google.gson.Gson;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class StartWindow extends Application {
    @SuppressWarnings("unchecked")

    @Override
    public void start(Stage primaryStage) {
        Button startNewGameButton = new Button("Start new Game");
        Button loadGameButton = new Button("Load Game");

        Text warningText = new Text("No saved files.");
        warningText.setFill(Color.RED);
        warningText.setVisible(false);

        ChoiceBox scoreDataChoiceBox = new ChoiceBox();
        scoreDataChoiceBox.setVisible(false);
        File scoreDataDirectory = new File("scoredata");
        if (scoreDataDirectory.exists()) {
            for (File file : Objects.requireNonNull(scoreDataDirectory.listFiles())) {
                scoreDataChoiceBox.getItems().add(file.getName());  //gespeicherte files zu choicebox hinzufügen
            }
        }

        startNewGameButton.setOnAction(event -> {
            Main mainGame = new Main();
            primaryStage.close();
        });

        loadGameButton.setOnAction(event -> {
            File directory = new File("scoredata");
            scoreDataChoiceBox.setVisible(directory.exists());
            warningText.setVisible(!directory.exists());
        });

        scoreDataChoiceBox.setOnAction(event -> {
            String chosenScoreData = scoreDataChoiceBox.getValue().toString();
            MainMenu.currentLoadedFile = "scoredata/" + chosenScoreData;

            try {
                Scanner scanner = new Scanner(new File("scoredata/" + chosenScoreData));
                String minefieldString = scanner.nextLine();
                SaveMinefield minefieldToSave = new Gson().fromJson(minefieldString, SaveMinefield.class);
                SaveZelle[][] zellenToSave = new SaveZelle[minefieldToSave.hoehe][minefieldToSave.breite];
                for (int x = 0; x < minefieldToSave.hoehe; x++) {
                    for (int y = 0; y < minefieldToSave.breite; y++) {
                        zellenToSave[x][y] = new Gson().fromJson(scanner.nextLine(), SaveZelle.class);
                    }
                }
                Minefield loadMinefield = new Minefield(minefieldToSave, zellenToSave);

                Main.prime = primaryStage;
                primaryStage.setTitle("Minesweeper");
                primaryStage.setResizable(false);
                Main.neustart(loadMinefield);
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(startNewGameButton, loadGameButton);

        BorderPane border = new BorderPane();
        border.setCenter(vBox);
        border.setBottom(warningText);
        border.setRight(scoreDataChoiceBox);

        Pane startWindowVbox = new Pane(border);
        Scene scene = new Scene(startWindowVbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
