import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.util.Duration;

import java.util.Random;

/**
 * Das Spielfeld
 */
@SuppressWarnings("unused")
public class Minefield extends SaveMinefield {
    private Zelle[][] zellen;
    public Menu timeMenu;
    public Menu minenMenu;
    public int totalMinen = 0;
    public boolean firstClick = false;
    //public int aufgedeckteZellen = 0;
    //public int beflaggteMinen = 0;

    public Minefield(int hoehe, int breite) {
        super();
        this.breite = breite;
        this.hoehe = hoehe;
        this.zellen = new Zelle[hoehe][breite];
        for (int x = 0; x < hoehe; x++) {
            for (int y = 0; y < breite; y++) {
                //alle möglichen nachbar muessen erstmal existieren dessewegen muessen biede Schliefen
                //obwohl sie identisch sind existieren
                zellen[x][y] = new Zelle(this);
            }
        }
        initNachbarn();
        this.timeMenu = new Menu(stringTime(0));
        this.minenMenu = new Menu(stringMinen(0));
        doTime();
        doMinen();
    }

    /**
     * Neue Minefield Instanz aus SaveMinefield und SaveZellen ArrayArray
     * @param fieldFromSave
     * @param zellenFromSave
     */
    public Minefield(SaveMinefield fieldFromSave, SaveZelle[][] zellenFromSave) {
        super(fieldFromSave.hoehe, fieldFromSave.breite, fieldFromSave.anzahlMinen, fieldFromSave.ended, fieldFromSave.isInitialized, fieldFromSave.seconds);
        this.zellen = new Zelle[hoehe][breite];
        for (int x = 0; x < hoehe; x++) {
            for (int y = 0; y < breite; y++) {
                //alle möglichen nachbar muessen erstmal existieren dessewegen muessen biede Schliefen
                //obwohl sie identisch sind existieren
                zellen[x][y] = new Zelle(this, zellenFromSave[x][y]);
            }
        }
        initNachbarn();
        this.timeMenu = new Menu(stringTime(this.seconds));
        this.minenMenu = new Menu(stringMinen(getBeflaggte()));
        doTime();
        doMinen();
    }

    private void initNachbarn() {
        for (int x = 0; x < hoehe; x++) {
            for (int y = 0; y < breite; y++) {
                //setzen der Nachbarn fuer jede Zelle
                //rechts
                if (y < breite-1) {
                    zellen[x][y].getNachbarn().add(zellen[x][y+1]);
                }

                //oben rechts
                if (x > 0 && y < breite-1) {
                    zellen[x][y].getNachbarn().add(zellen[x-1][y+1]);
                }

                //oben
                if (x > 0) {
                    zellen[x][y].getNachbarn().add(zellen[x-1][y]);
                }

                //oben links
                if (x > 0 && y > 0) {
                    zellen[x][y].getNachbarn().add(zellen[x-1][y-1]);
                }

                //links
                if (y > 0) {
                    zellen[x][y].getNachbarn().add(zellen[x][y-1]);
                }

                //unten links
                if (x < hoehe-1 && y > 0) {
                    zellen[x][y].getNachbarn().add(zellen[x+1][y-1]);
                }

                //unten
                if (x < hoehe-1) {
                    zellen[x][y].getNachbarn().add(zellen[x+1][y]);
                }

                //unten rechts
                if (x < hoehe-1 && y < breite-1) {
                    zellen[x][y].getNachbarn().add(zellen[x+1][y+1]);
                }
            }
        }
    }

    /**
     * Bereitet das Spielfeld vor
     * @param num gewuenschte Anzahl Minen
     */
    public void setMines(int num)  {
        anzahlMinen = num;
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            int randintX = random.nextInt(hoehe);
            int randintY = random.nextInt(breite);
            if (zellen[randintX][randintY].istMine()) {
                //falls Zelle bereits Mine enthaelt, soll erneut versucht werden
                num++;
            }
            zellen[randintX][randintY].setIstMine(true);
        }
    }


    /**
     * ermittelt fuer alle Zellen die Anzahl angrenzender Minen
     */
    public void checkNachbarn() {
        for (int x = 0; x < hoehe; x++) {
            for (int y = 0; y < breite; y++) {
                for (Zelle z : zellen[x][y].getNachbarn()) {
                    if (z.istMine()) {
                        zellen[x][y].setBenachbarteMinen(zellen[x][y].getBenachbarteMinen()+1);
                    }
                }
            }
        }
        this.isInitialized = true;
        System.out.println(this);
    }


    public String toString() {
        StringBuilder out = new StringBuilder("\n");
        for (int x = 0; x < hoehe; x++) {
            for (int y = 0; y < breite; y++) {
                if (zellen[x][y].istMine()) {
                    out.append("#");
                } else {
                    out.append(zellen[x][y].getBenachbarteMinen());
                }
            }
            out.append("\n");
        }
        return out.toString();
    }

    public void checkIfWon() {
        //TODO: Fertig/Nochmal Stage?
        for ( Zelle[] zeilen : zellen) {
            for (Zelle zelle : zeilen) {
                if (!zelle.istAufgedeckt()) {
                    if(!zelle.istMarkiert()) {
                        return;
                    }
                }
            }
        }
        ended = 1;
        System.out.println("AllClear");
    }

    public Zelle getZelle(int x, int y) {
        return zellen[y][x];
    }

    public void setZelle(int x, int y, Zelle zelle) {
        zellen[y][x] = zelle;
    }

    public Zelle[][] getZellen() {
        return zellen;
    }

    public void setZellen(Zelle[][] zellen) {
        this.zellen = zellen;
    }

    public void doTime(){
        /*if(appendText() == 1){
            return;
        }*/
        Timeline timeline  = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(firstClick == true){
                    checkIfWon();
                    seconds++;
                    timeMenu.setText(stringTime(seconds));
                    if(appendText() == 1){
                        timeline.stop();
                    }
                }
            }
        });
        timeline.getKeyFrames().add(frame);
        timeline.playFromStart();
    }

    public static String stringTime(int second){
        int minute = second/60;
        int hour = minute/60;
        second = second%60;
        String s = String.valueOf(second);
        if(second < 10){ s = "0" + s; }
        String m = String.valueOf(minute);
        if(minute < 10){ m = "0" + m; }
        String h = String.valueOf(hour);
        if(hour < 10){ h = "0" + h; }
        return h+":"+m+":"+s;
    }

    public int appendText(){
        if(ended!=0 ){
            if(ended == 1){
                timeMenu.setText(stringTime(seconds) + " :Victory");
            }
            else if(ended == -1){
                timeMenu.setText(stringTime(seconds) + " :Defeat");
            }
            return 1;
        }
        return 0;
    }

    public void doMinen(){
        int minen = anzahlMinen;
        Timeline timeline  = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(0.01), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int beflaggte = getBeflaggte();
                minenMenu.setText(stringMinen(beflaggte));
                if(ended!=0){
                    timeline.stop();
                }
            }
        });
        timeline.getKeyFrames().add(frame);
        timeline.playFromStart();
    }

    public String stringMinen(int beflaggte){
        return String.valueOf(beflaggte) + '/' + String.valueOf(totalMinen);
    }

    public int getBeflaggte(){
        int beflaggte = 0;
        for (int x = 0; x < hoehe; x++) {
            for (int y = 0; y < breite; y++) {
                if(zellen[x][y].istMarkiert() == true){
                    beflaggte++;
                }
            }
        }
        return beflaggte;
    }
}
