/**
 * serialisierbares Parent der Minefield Klasse
 */
public class SaveMinefield {
    protected int hoehe;
    protected int breite;
    protected int anzahlMinen = 0;
    protected int ended = 0;
    protected boolean isInitialized = false;
    protected int seconds = 0;

    SaveMinefield() {}

    /**
     * Neue Instanz aus Minefield Objekt
     * @param minefield
     */
    public SaveMinefield(Minefield minefield) {
        this.hoehe = minefield.hoehe;
        this.breite = minefield.breite;
        this.anzahlMinen = minefield.anzahlMinen;
        this.ended = minefield.ended;
        this.isInitialized = minefield.isInitialized;
        this.seconds = minefield.seconds;
    }

    public SaveMinefield(int hoehe, int breite, int anzahlMinen, int ended, boolean isInitialized, int seconds) {
        this.hoehe = hoehe;
        this.breite = breite;
        this.anzahlMinen = anzahlMinen;
        this.ended = ended;
        this.isInitialized = isInitialized;
        this.seconds = seconds;
    }

    public int isEnded() {
        return ended;
    }

    public void setIsEnded(int ended) {
        this.ended = ended;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public void setEnded(int ended) {
        this.ended = ended;
    }

}
