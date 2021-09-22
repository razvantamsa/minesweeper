import com.google.gson.Gson;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class SliderSchwierigkeitsgrad extends Stage {

    //Innere Klasse, um Difficulties zu speichern
    public static class Difficulty {
        int spielbrettHoehe;
        int spielbrettBreite;
        int minenAnzahl;

        public Difficulty(int hoehe, int breite, int minenAnzahl) {
            this.spielbrettHoehe = hoehe;
            this.spielbrettBreite = breite;
            this.minenAnzahl = minenAnzahl;
        }
    }

    double spielbrettHoehe = Main.spielbrettHoehe;
    double spielbrettBreite = Main.spielbrettBreite;
    double minenAnzahl = Main.minenAnzahl;

    String name;

    Slider sliderSpielbrettHoehe = new Slider(5, 50, spielbrettHoehe);
    Slider sliderSpielbrettBreite = new Slider(5, 50, spielbrettBreite);
    Slider sliderMinenAnzahl = new Slider(0, ((sliderSpielbrettBreite.getValue() * sliderSpielbrettHoehe.getValue()) - 1), minenAnzahl);

    Boolean diffCommitted = false; //überprüfe, ob diff committet wurde

    SliderSchwierigkeitsgrad() {
        this.setTitle("Schwierigkeitsgrad Einstellungen");

        // Slider für Spielbretthöhe, -breite und Minenanzahl
        Label labelSpielbrettHoehe = new Label("Hoehe des Spielbretts: 8");
        sliderSpielbrettHoehe.setPrefWidth(1000);
        sliderSpielbrettHoehe.setMajorTickUnit(5);
        sliderSpielbrettHoehe.setMinorTickCount(4);
        sliderSpielbrettHoehe.setShowTickMarks(true);   //zeige Striche
        sliderSpielbrettHoehe.setShowTickLabels(true);  //zeige Zahlen
        sliderSpielbrettHoehe.setSnapToTicks(true);     //springe zu nächstliegendem Strich
        sliderSpielbrettHoehe.setBlockIncrement(1.0);

        Label labelSpielbrettBreite = new Label("Breite des Spielbretts: 8");
        sliderSpielbrettBreite.setPrefWidth(1000);
        sliderSpielbrettBreite.setMajorTickUnit(5);
        sliderSpielbrettBreite.setMinorTickCount(4);
        sliderSpielbrettBreite.setShowTickMarks(true);
        sliderSpielbrettBreite.setShowTickLabels(true);
        sliderSpielbrettBreite.setSnapToTicks(true);
        sliderSpielbrettBreite.setBlockIncrement(1);

        Label labelMinenAnzahl = new Label("Minenanzahl: 10");
        sliderMinenAnzahl.setPrefWidth(1000);
        sliderMinenAnzahl.setMajorTickUnit(10);
        sliderMinenAnzahl.setMinorTickCount(9);
        sliderMinenAnzahl.setShowTickMarks(true);
        sliderMinenAnzahl.setShowTickLabels(true);
        sliderMinenAnzahl.setSnapToTicks(true);
        sliderMinenAnzahl.setBlockIncrement(1);

        // Listener für die Slider von Höhe, Breite, Minenanzahl
        sliderSpielbrettHoehe.valueProperty().addListener((observable, oldValue, newValue) -> {
            spielbrettHoehe = sliderSpielbrettHoehe.getValue();
            labelSpielbrettHoehe.setText("Hoehe des Spielbretts: " + (int) spielbrettHoehe);
            sliderMinenAnzahl.setMax(spielbrettHoehe * sliderSpielbrettBreite.getValue());
        });

        sliderSpielbrettBreite.valueProperty().addListener((observable, oldValue, newValue) -> {
            spielbrettBreite = sliderSpielbrettBreite.getValue();
            labelSpielbrettBreite.setText("Breite des Spielbretts: " + (int) spielbrettBreite);
            sliderMinenAnzahl.setMax(spielbrettBreite * sliderSpielbrettHoehe.getValue());
        });

        sliderMinenAnzahl.valueProperty().addListener((observable, oldValue, newValue) -> {
            minenAnzahl = sliderMinenAnzahl.getValue();
            labelMinenAnzahl.setText("Minenanzahl: " + (int) minenAnzahl);
            sliderMinenAnzahl.setMax(sliderSpielbrettHoehe.getValue() * sliderSpielbrettBreite.getValue());
        });

        // HBoxen für die Slider und Labels von Höhe, Breite und Minenanzahl
        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(sliderSpielbrettHoehe, labelSpielbrettHoehe);
        hBox1.setLayoutX(50);
        hBox1.setLayoutY(50);

        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(sliderSpielbrettBreite, labelSpielbrettBreite);
        hBox2.setLayoutX(50);
        hBox2.setLayoutY(100);

        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(sliderMinenAnzahl, labelMinenAnzahl);
        hBox3.setLayoutX(50);
        hBox3.setLayoutY(150);

        Pane movableSliderPane = new Pane();
        movableSliderPane.getChildren().addAll(hBox1, hBox2, hBox3);


        // Menu und Submenus zum Laden und Abspeichern der Schwierigkeitsgrade
        Menu difficultyMenu = new Menu("Main Menu");

        // Obermenus: speichern und laden
        Menu saveSubMenu = new Menu("Save as");
        Menu loadSubMenu = new Menu("Load");
        difficultyMenu.getItems().addAll(saveSubMenu, loadSubMenu);

        // Submenu save: new, um neue diffs zu erstellen
        MenuItem newDiffItem = new MenuItem("New");
        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        saveSubMenu.getItems().addAll(newDiffItem, separatorMenuItem1);

        // Submenu load: drei voreingestellte diffs
        RadioMenuItem easyDiffIteam = new RadioMenuItem("Easy");
        easyDiffIteam.setSelected(true);
        RadioMenuItem mediumDiffIteam = new RadioMenuItem("Medium");
        RadioMenuItem hardDiffIteam = new RadioMenuItem("Hard");
        SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();  //Trennlinie

        //voreingestellte diffs werden exklusiv mit Haken versehen, wenn sie angeklickt werden
        ToggleGroup loadToggleGroup = new ToggleGroup();
        loadToggleGroup.getToggles().addAll(easyDiffIteam, mediumDiffIteam, hardDiffIteam);
        loadSubMenu.getItems().addAll(easyDiffIteam, mediumDiffIteam, hardDiffIteam, separatorMenuItem2);

        //voreingestellte diffs verändern die Spielvariablen und Slider, wenn sie ausgewählt werden
        easyDiffIteam.setOnAction(event -> {
            Main.spielbrettHoehe = 8;
            Main.spielbrettBreite = 8;
            Main.minenAnzahl = 10;

            sliderSpielbrettHoehe.setValue(8);
            sliderSpielbrettBreite.setValue(8);
            sliderMinenAnzahl.setValue(10);
        });
        mediumDiffIteam.setOnAction(event -> {
            Main.spielbrettHoehe = 12;
            Main.spielbrettBreite = 12;
            Main.minenAnzahl = 20;

            sliderSpielbrettHoehe.setValue(12);
            sliderSpielbrettBreite.setValue(12);
            sliderMinenAnzahl.setValue(20);
        });
        hardDiffIteam.setOnAction(event -> {
            Main.spielbrettHoehe = 15;
            Main.spielbrettBreite = 15;
            Main.minenAnzahl = 30;

            sliderSpielbrettHoehe.setValue(15);
            sliderSpielbrettBreite.setValue(15);
            sliderMinenAnzahl.setValue(30);
        });


        // Lade initial die Dateien "Difficulties/filename" als Menuitems der Submenus "Save" und "Load"
        File initDirectory = new File("Difficulties");
        if (initDirectory.exists()) {
            for (File file : Objects.requireNonNull(initDirectory.listFiles())) {
                MenuItem saveDiffItem = new MenuItem(file.getName());
                RadioMenuItem loadDiffItem = new RadioMenuItem(file.getName()); //damit Item mit exklusivem Haken versehen wird beim Anklicken
                saveSubMenu.getItems().add(saveDiffItem);
                loadSubMenu.getItems().add(loadDiffItem);
                loadToggleGroup.getToggles().add(loadDiffItem);

                String pathname = "Difficulties/" + file.getName();
                saveDiffItem.setOnAction(event1 -> {
                    loadDiffItem.setSelected(true);
                    try {
                        saveDiff(pathname);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                loadDiffItem.setOnAction(event1 -> {
                    loadDiffItem.setSelected(true);
                    try {
                        loadDiff(pathname);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                });
            }

        }


        newDiffItem.setOnAction(event -> {

            Text text = new Text("Enter a name for your difficulty");
            TextField textFieldNewDiff = new TextField();
            textFieldNewDiff.setMaxWidth(200);

            Button buttonNewDiff = new Button("Save");
            VBox vBoxNewDiff = new VBox(text, textFieldNewDiff, buttonNewDiff);
            Scene sceneNewDiff = new Scene(vBoxNewDiff, 300, 100);

            Stage stageNewDiff = new Stage();
            stageNewDiff.setTitle("Name for new difficulty");
            stageNewDiff.initStyle(StageStyle.DECORATED); //Rahmen und Buttons
            stageNewDiff.initModality(Modality.APPLICATION_MODAL); //blockiert andere Fenster
            stageNewDiff.setScene(sceneNewDiff);
            stageNewDiff.show();

            buttonNewDiff.setOnAction(value -> {
                File directory = new File("Difficulties");
                List<String> allFiles = new ArrayList<>();

                if (!directory.exists()) {
                    directory.mkdir();
                } else {
                    for (File file : Objects.requireNonNull(directory.listFiles())) {
                        allFiles.add(file.getName());
                    }
                }

                if (!textFieldNewDiff.getText().isBlank() && !allFiles.contains(textFieldNewDiff.getText())) {
                    stageNewDiff.close();
                    name = textFieldNewDiff.getText();
                    text.setFill(Color.BLACK);

                    try {
                        File newDiff = new File("Difficulties/" + name);
                        newDiff.createNewFile();

                        saveDiff("Difficulties/" + name);

                        MenuItem saveDiffItem = new MenuItem(name);
                        RadioMenuItem loadDiffItem = new RadioMenuItem(name);
                        loadDiffItem.setSelected(true);
                        saveSubMenu.getItems().add(saveDiffItem);
                        loadSubMenu.getItems().add(loadDiffItem);
                        loadToggleGroup.getToggles().add(loadDiffItem);

                        String pathname = "Difficulties/" + name;
                        saveDiffItem.setOnAction(event1 -> {
                            loadDiffItem.setSelected(true);
                            try {
                                saveDiff(pathname);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        loadDiffItem.setOnAction(event1 -> {
                            loadDiffItem.setSelected(true);
                            try {
                                loadDiff(pathname);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    text.setText("Please enter a new name");
                    text.setFill(Color.RED);
                }
            });
        });


        MenuBar menuBarDifficulty = new MenuBar();
        menuBarDifficulty.getMenus().add(difficultyMenu);

        //Button zum committen des Schwierigkeitsgrades (Daten an spielfeld übertragen)
        Button commitDiffButton = new Button("Commit");
        commitDiffButton.setLayoutX(50);
        commitDiffButton.setLayoutY(0);
        commitDiffButton.setOnAction(event -> {
            diffCommitted = true;
            Main.spielbrettHoehe = (int) sliderSpielbrettHoehe.getValue();
            Main.spielbrettBreite = (int) sliderSpielbrettBreite.getValue();
            Main.minenAnzahl = (int) sliderMinenAnzahl.getValue();
        });

        //Button zum Zurücksetzen der Höhe, breite auf 8 und Minenanzahl auf 10

        Button defaultButton = new Button("Default");
        defaultButton.setLayoutX(50);
        defaultButton.setLayoutY(30);
        defaultButton.setOnAction(event -> {
            sliderSpielbrettHoehe.setValue(8);
            sliderSpielbrettBreite.setValue(8);
            sliderMinenAnzahl.setValue(10);
        });

        Pane buttonPane = new Pane();
        buttonPane.getChildren().addAll(commitDiffButton, defaultButton);


        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBarDifficulty, movableSliderPane, buttonPane);
        Scene scene = new Scene(vbox, 1250, 300);
        this.setScene(scene);


        //Wenn Schwierigkeitseinstellungen geschlossen werden, wird Spiel neugestartet
        this.setOnCloseRequest((event) -> {
            if (diffCommitted)
                Main.neustart(null);
        });

    }

    public void saveDiff(String pathname) throws IOException {
        BufferedWriter writerSaveDiff = new BufferedWriter(new FileWriter(pathname, false));
        writerSaveDiff.write(new Gson().toJson(new Difficulty((int) spielbrettHoehe, (int) spielbrettBreite, (int) minenAnzahl)));
        writerSaveDiff.close();
    }

    public void loadDiff(String pathname) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(pathname));
        String jsonString = scanner.nextLine();
        scanner.close();

        Difficulty difficulty = new Gson().fromJson(jsonString, Difficulty.class);
        Main.spielbrettHoehe = difficulty.spielbrettHoehe;
        Main.spielbrettBreite = difficulty.spielbrettBreite;
        Main.minenAnzahl = difficulty.minenAnzahl;

        sliderSpielbrettHoehe.setValue(difficulty.spielbrettHoehe);
        sliderSpielbrettBreite.setValue(difficulty.spielbrettBreite);
        sliderMinenAnzahl.setValue(difficulty.minenAnzahl);
    }
}



