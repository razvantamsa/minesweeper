import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public  class Main {
    //TODO Timer als SaveMinefield attribut damit die Zeit auch gespeichert werden kann
    // so wie Zoom Und Drag funktioniert, wird jedesmal eine neu Scene erzeugt
    // dh Timer wird auch zurück gesetzt, ist Problem
    //TODO AnzeigeMinen
    public static Stage prime;
    public static GridPane pane;
    public static MainMenu mainMenu;
    public static Minefield spielbrett;
    public static ZellButton[][] buttons;
    public static int spielbrettHoehe = 8, spielbrettBreite = 8, minenAnzahl = 10;

    //For drag Test
    public static int gridpaneMaxX = 20, gridpaneMaxY = 20;
    public static int buttonDim = 30, xCutStart = 0, yCutStart = 0, xDistance = 0, yDistance = 0;

    public Main() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Minesweeper");
        primaryStage.setResizable(false);
        prime = primaryStage;

        setSpielbrett(new Minefield(spielbrettHoehe, spielbrettBreite));
        setPane();
        mainMenu = new MainMenu();
        mainMenu.getMenus().addAll(spielbrett.minenMenu, spielbrett.timeMenu);
        Scene scene = new Scene(new VBox(mainMenu, pane));
        scene.setFill(new Color(0.6706,0.6706,0.6706,1));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Aktualisiert Zelldarstellung im ganzen Gitter
     * @param lose true falls mine aufgedeckt wurde
     */
    public static void reDoField(boolean lose) {
        for ( ZellButton[] zeilen : buttons) {
            for (ZellButton zelle : zeilen) {
                if (lose) {
                    if (zelle.getZelle().istMine()) {
                        zelle.setText("#");
                        zelle.setStyle("-fx-background-color: #876d6d");
                    }
                } else {
                    if (zelle.getZelle().istMarkiert()) {
                        zelle.setText("⚑");
                    }
                    if (zelle.getZelle().istAufgedeckt()) {
                        if (!zelle.getZelle().istMarkiert()) {
                            zelle.setStyle("-fx-background-color: #ababab");
                        }
                        if (zelle.getZelle().getBenachbarteMinen() == 0) {
                            zelle.setText(" ");
                        } else {
                            zelle.setText(Integer.toString(zelle.getZelle().getBenachbarteMinen()));
                        }
                    }
                }
            }
            //System.out.println(spielbrett.anzahlMinen);
        }
    }

    /**
     *Setzt spielbrett und erzeugt button field als jeweilige erweiterung einer Zelle oder neues Spielbrett
     * mit alten werten (Übergabe Minefield = null)
     */
    public static void setSpielbrett(Minefield minefield) {
        if (minefield == null) {
            minefield = new Minefield(spielbrettHoehe, spielbrettBreite);
        }
        spielbrett = minefield;
        spielbrettBreite = minefield.breite;
        spielbrettHoehe = minefield.hoehe;
        buttons = new ZellButton[spielbrettHoehe][spielbrettBreite];

        for (int y = 0; y < minefield.hoehe; y++) {
            for (int x = 0; x < minefield.breite; x++) {
                buttons[y][x] = new ZellButton(spielbrett.getZellen()[y][x]);
                buttons[y][x].setFont(new Font(buttonDim/2.5));//vorläufig
            }
        }
    }

    public static void setPane() {
        //TODO vorerst immer links oben anfangen
        pane = new GridPane();
        pane.setMaxSize(gridpaneMaxX*buttonDim, gridpaneMaxY*buttonDim); //30 angeneheme Buttongroesse
        pane.setMinSize(gridpaneMaxX*buttonDim, gridpaneMaxY*buttonDim);
        int xOffset = 0;
        int yOffset = 0;
        if (spielbrett.hoehe < gridpaneMaxY && spielbrett.breite < gridpaneMaxX) {
            //wir wollen bei kleinen feldern in der mitte blieben
            xOffset = (gridpaneMaxX - spielbrettBreite)/2;
            yOffset = (gridpaneMaxY - spielbrettHoehe)/2;
            System.out.println(xOffset + "," + yOffset);
        }
        for (int y = yCutStart; y < gridpaneMaxY + yCutStart && y < spielbrettHoehe; y++) {
            for (int x = xCutStart; x < gridpaneMaxX + xCutStart && x < spielbrettBreite; x++) {
                if ((x < xOffset) || (y < yOffset)) {
                    //freien Platz mit leeren Label auffüllen
                    Label placeholder = new Label();
                    placeholder.setMaxSize(buttonDim, buttonDim);
                    placeholder.setMinSize(buttonDim, buttonDim);
                    pane.add(placeholder, x, y, 1,1);
                }
                pane.add(buttons[y][x], x+xOffset, y+yOffset, 1,1);
            }
        }
    }

    public static void neustart(Minefield minefield) {
        if (minefield == null) {
            setSpielbrett(new Minefield(spielbrettHoehe, spielbrettBreite));
        } else if (!minefield.equals(spielbrett)) {
            setSpielbrett(minefield);
            spielbrett.totalMinen = minefield.anzahlMinen;
        }
        xCutStart = 0;
        yCutStart = 0;
        setPane();
        mainMenu = new MainMenu();
        mainMenu.getMenus().addAll(spielbrett.minenMenu, spielbrett.timeMenu);
        prime.setScene(new Scene(new VBox(mainMenu, Main.pane)));
        reDoField(false);
    }

    public static void dragHappend() {
        int travXCells = xDistance/buttonDim;
        int travYCells = yDistance/buttonDim;
        xCutStart -= travXCells;
        yCutStart -= travYCells;
        if (xCutStart < 0) {
            xCutStart = 0;
        }
        if (yCutStart < 0 ) {
            yCutStart = 0;
        }
        if (spielbrettHoehe - gridpaneMaxY > 0 && yCutStart > spielbrettHoehe - gridpaneMaxY) {
            yCutStart = spielbrettHoehe - gridpaneMaxY;
        }
        if (spielbrettBreite - gridpaneMaxX > 0 && xCutStart > spielbrettBreite - gridpaneMaxX) {
            xCutStart = spielbrettBreite - gridpaneMaxX;
        }
        setPane();
        mainMenu = new MainMenu();
        mainMenu.getMenus().addAll(spielbrett.minenMenu, spielbrett.timeMenu);
        Scene scene = new Scene(new VBox(mainMenu, Main.pane));
        scene.setFill(new Color(0.6706,0.6706,0.6706,1));
        prime.setScene(scene);
        reDoField(false);

    }

    public static void scrollHappend(double scroll) {
        //TODO Was wenn spielfeld stark rechteckig?
        //TODO ausrichten nach Zelle in welcher drag passiert?(toomuch?)
        //TODO bei sehr großen gridpaneMax kommt irgentdwas durcheinander
        // wieder rein zoomen funktioniert dann kaum
        gridpaneMaxX += scroll/2;
        gridpaneMaxY += scroll/2;
        //daraufhin buttondim so waehlen dass gridpane gleich lang bleibt
        // also Produkt aus buttonDim gridpaneMax soll seiin 600;
        buttonDim = 600/gridpaneMaxX;
        setSpielbrett(spielbrett);
        setPane();
        mainMenu = new MainMenu();
        mainMenu.getMenus().addAll(spielbrett.minenMenu, spielbrett.timeMenu);
        Scene scene = new Scene(new VBox(mainMenu, Main.pane));
        scene.setFill(new Color(0.6706,0.6706,0.6706,1));
        prime.setScene(scene);
        reDoField(false);

    }
}
