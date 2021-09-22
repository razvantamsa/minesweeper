import com.google.gson.Gson;
import javafx.scene.control.*;
import javafx.scene.Scene;
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

//TODO: häkchen setzen
//TODO: save disableproperty überarbeiten
//TODO: file entfernen wenn game zuende

public class MainMenu extends MenuBar {
    static String currentLoadedFile;

    public MainMenu() {
        Menu mainMenu = new Menu("Main");

        //Menu 1
        MenuItem endMenuItem = new MenuItem("End");
        MenuItem restartMenuItem = new MenuItem("Restart");
        MenuItem setDiffMenuItem = new MenuItem("Difficulty");

        endMenuItem.setOnAction(action -> Main.prime.close());

        restartMenuItem.setOnAction(action -> {
            Main.neustart(null);
        });

        setDiffMenuItem.setOnAction(action -> {
            //Erzeugen von Schwierigkeits Stage und binden an primaryStage
            Stage stageSliderDiff = new SliderSchwierigkeitsgrad();
            stageSliderDiff.initOwner(Main.prime);
            stageSliderDiff.initStyle(StageStyle.DECORATED);
            stageSliderDiff.show();
        });

        mainMenu.getItems().addAll(endMenuItem, restartMenuItem, setDiffMenuItem);


        //Menu 2
        Menu gameMenu = new Menu("Game");

        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.disableProperty().setValue(true);
        Menu saveAsSubMenu = new Menu("Save As");
        Menu loadSubMenu = new Menu("Load");
        ToggleGroup loadToggleGroup = new ToggleGroup();

        MenuItem saveNewMenuItem = new MenuItem("New");
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        saveAsSubMenu.getItems().addAll(saveNewMenuItem, separatorMenuItem);

        //initiale Files als Items laden
        File initDirectory = new File("scoredata");
        if (initDirectory.exists()) {
            for (File file : Objects.requireNonNull(initDirectory.listFiles())) {
                MenuItem saveAsGameItem = new MenuItem(file.getName());
                RadioMenuItem loadGameItem = new RadioMenuItem(file.getName());
                saveAsSubMenu.getItems().add(saveAsGameItem);
                loadSubMenu.getItems().add(loadGameItem);
                loadToggleGroup.getToggles().add(loadGameItem);

                String pathname = "scoredata/" + file.getName();
                saveAsGameItem.setOnAction(event1 -> {
                    saveMenuItem.disableProperty().setValue(false);
                    loadGameItem.setSelected(true);
                    try {
                        saveScoreData(pathname);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                loadGameItem.setOnAction(event -> {
                    saveMenuItem.disableProperty().setValue(false);
                    loadGameItem.setSelected(true);
                    try {
                        loadScoreData(pathname);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        saveMenuItem.setOnAction(event -> {
            String pathname = currentLoadedFile;
            try {
                saveScoreData(pathname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        saveNewMenuItem.setOnAction(event -> {
            Text text = new Text("Enter a name for your current Score Data");
            TextField textFieldNewScoreData = new TextField();
            textFieldNewScoreData.setMaxWidth(200);

            Button buttonNewScoreData = new Button("Save");
            VBox vBoxNewScoreData = new VBox(text, textFieldNewScoreData, buttonNewScoreData);
            Scene sceneNewScoreData = new Scene(vBoxNewScoreData, 300, 100);

            Stage stageNewScoreData = new Stage();
            stageNewScoreData.setTitle("New Score Data");
            stageNewScoreData.initStyle(StageStyle.DECORATED); //Rahmen und Buttons
            stageNewScoreData.initModality(Modality.APPLICATION_MODAL); //blockiert andere Fenster
            stageNewScoreData.setScene(sceneNewScoreData);
            stageNewScoreData.show();

            buttonNewScoreData.setOnAction(value -> {
                File directory = new File("scoredata");
                List<String> allFiles = new ArrayList<>();

                if (!directory.exists()) {
                    directory.mkdir();
                } else {
                    for (File file : Objects.requireNonNull(directory.listFiles())) {
                        allFiles.add(file.getName());       //später überprüfen, ob name schon vergeben ist, dem man neuen file geben will
                    }
                }

                if (!textFieldNewScoreData.getText().isBlank() && !allFiles.contains(textFieldNewScoreData.getText())) {
                    saveMenuItem.disableProperty().setValue(false); //Spielstand kann unter neuem Stand gespeichert werden
                    currentLoadedFile = textFieldNewScoreData.getText();  //aktualisiere Namen des aktuell geladenen games
                    stageNewScoreData.close();
                    String name = textFieldNewScoreData.getText();
                    text.setFill(Color.BLACK);

                    try {
                        new File("scoredata/" + name).createNewFile();
                        saveScoreData("scoredata/" + name);

                        MenuItem saveAsGameItem = new MenuItem(name);
                        RadioMenuItem loadGameItem = new RadioMenuItem(name);
                        loadGameItem.setSelected(true);
                        saveAsSubMenu.getItems().add(saveAsGameItem);
                        loadSubMenu.getItems().add(loadGameItem);
                        loadToggleGroup.getToggles().add(loadGameItem);

                        String pathname = "scoredata/" + name;
                        saveAsGameItem.setOnAction(event1 -> {
                            saveMenuItem.disableProperty().setValue(false);
                            loadGameItem.setSelected(true);
                            try {
                                saveScoreData(pathname);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        loadGameItem.setOnAction(event1 -> {
                            saveMenuItem.disableProperty().setValue(false);
                            loadGameItem.setSelected(true);
                            try {
                                loadScoreData(pathname);
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

        gameMenu.getItems().addAll(saveMenuItem, saveAsSubMenu, loadSubMenu);
        this.getMenus().addAll(mainMenu, gameMenu);
    }

    public void saveScoreData(String pathname) throws IOException {
        currentLoadedFile = pathname;

        BufferedWriter writerNewDiff = new BufferedWriter(new FileWriter(pathname, false));
        writerNewDiff.write(new Gson().toJson(new SaveMinefield(Main.spielbrett)));
        for (Zelle[] zeile : Main.spielbrett.getZellen()) {
            for (Zelle z : zeile) {
                writerNewDiff.newLine();
                writerNewDiff.write(new Gson().toJson(new SaveZelle(z)));
            }
        }
        writerNewDiff.close();
    }

    public void loadScoreData(String pathname) throws FileNotFoundException {
        currentLoadedFile = pathname;

        Scanner scanner = new Scanner(new File(pathname));
        String minefieldString = scanner.nextLine();
        SaveMinefield minefieldToSave = new Gson().fromJson(minefieldString, SaveMinefield.class);
        SaveZelle[][] zellenToSave = new SaveZelle[minefieldToSave.hoehe][minefieldToSave.breite];
        for (int x = 0; x < minefieldToSave.hoehe; x++) {
            for (int y = 0; y < minefieldToSave.breite; y++) {
                zellenToSave[x][y] = new Gson().fromJson(scanner.nextLine(), SaveZelle.class);
            }
        }
        scanner.close();

        Minefield loadMinefield = new Minefield(minefieldToSave, zellenToSave);
        Main.neustart(loadMinefield);

    }
}
