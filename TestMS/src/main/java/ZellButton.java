import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;

/**
 * Beschreibt Verhalten einer MinesweeperZelle
 */
public class ZellButton extends Button {
    private Zelle zelle;
    private int maxDim = Main.buttonDim;
    private double xDragStart, yDragStart, xDragEnd, yDragEnd;


    ZellButton(Zelle zelle) {
        this.zelle = zelle;

        this.setMaxSize(maxDim,maxDim);
        this.setMinSize(maxDim,maxDim);

        this.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                xDragStart = event.getX();
                yDragStart = event.getY();
            }
        });

        this.setOnMouseReleased(event -> {
            if(zelle.getField().firstClick == false){
                zelle.getField().firstClick = true;
            }
            if (event.getButton() == MouseButton.PRIMARY && zelle.getField().isEnded() == 0) {
                if (!zelle.getField().isInitialized()) {
                    //Erster gedrueckter Button
                    zelle.setIstMine(true); //um diese Zelle vor neuer Mine zu schuetzen
                    for (Zelle z: zelle.getNachbarn()) {
                        z.setIstMine(true); // das geliche für alle Nachbar sodass erste Zelle = 0
                    }
                    zelle.getField().setMines(Main.minenAnzahl);
                    zelle.getField().totalMinen = Main.minenAnzahl;
                    zelle.setIstMine(false);
                    for (Zelle z: zelle.getNachbarn()) {
                        z.setIstMine(false); // das geliche für alle Nachbar sodass erste Zelle = 0
                    }
                    zelle.getField().checkNachbarn();
                    aufdecken();
                    Main.reDoField(false);
                } else {
                    if (!zelle.istMarkiert()) { //wenn Zelle ist nicht beflaggt
                        if (zelle.istMine()) {
                            //wenn geklickte Zelle Mine
                            Main.reDoField(true); //aufdecken aller Minen
                            zelle.getField().setEnded(-1);
                            System.out.println("Sorry, you lose");
                        } else if (zelle.getBenachbarteMinen() == 0) {
                            aufdecken();
                            Main.reDoField(false);
                        } else {
                            aufdecken();
                            this.setText(Integer.toString(zelle.getBenachbarteMinen()));
                        }
                    }
                }
            }

            if (event.getButton() == MouseButton.SECONDARY) {
                xDragEnd = event.getX();
                yDragEnd = event.getY();
                int xDistance = (int) (xDragEnd - xDragStart);
                int yDistance = (int) (yDragEnd - yDragStart);
                if (Math.abs(xDistance) < 5 && Math.abs(yDistance) < 5 && zelle.getField().isEnded()==0 && !zelle.istAufgedeckt()) {
                    //normales MakierEvent
                    if(zelle.istMarkiert()){ // wenn beflaggt -> leer
                        zelle.setIstMarkiert(false);
                        this.setText("");
                        zelle.getField().anzahlMinen++;
                    }
                    else if(!zelle.istMarkiert()){ // wenn leer -> beflaggt
                        zelle.setIstMarkiert(true);
                        this.setText("⚑");
                        zelle.getField().anzahlMinen--;
                    }
                } else {
                    //DragEvent
                    Main.xDistance = xDistance;
                    Main.yDistance = yDistance;
                    if (Main.spielbrettBreite > Main.gridpaneMaxX && Main.spielbrettHoehe > Main.gridpaneMaxY) {
                        Main.dragHappend();
                    }
                }
            }
        });

        this.setOnScrollFinished(event -> {
            Main.scrollHappend(event.getTotalDeltaX());
        });
    }

    /**
     * deckt Zelle auf, ggf fuer alle Nachbarn ohne angraenzende Mine
     */
    public void aufdecken() {
        if (!zelle.istMarkiert()) { //damit Zellen die bereits Markiert sind nicht als aufgedeckt deklariert werden
            zelle.setIstAufgedeckt(true);
            this.setStyle("-fx-background-color: #ababab");
        }
        if (zelle.getBenachbarteMinen() == 0) {
            for (Zelle z : zelle.getNachbarn()) {
                if (!z.istAufgedeckt) { //quasi Rekursions-Endbedingung
                    z.aufdecken();
                }
            }
        }
    }

    public Zelle getZelle() {
        return zelle;
    }
}
