import java.util.ArrayList;

/**
 * Repraesentiert eine einzelnen Zellen des Minenfeldes
 */
public class Zelle extends SaveZelle{
    private ArrayList<Zelle> nachbarn;
    private Minefield field;

    public Zelle(Minefield field) {
        super();
        nachbarn = new ArrayList<>();
        this.field = field;
    }

    /**
     * Neue Instanz aus Zelle Objekt
     * @param field
     * @param fromSave
     */
    public Zelle (Minefield field, SaveZelle fromSave) {
        this.istMine = fromSave.istMine;
        this.istAufgedeckt = fromSave.istAufgedeckt;
        this.istMarkiert = fromSave.istMarkiert;
        this.benachbarteMinen = fromSave.benachbarteMinen;
        nachbarn = new ArrayList<>();
        this.field = field;
    }

    /**
     * deckt Zelle auf, ggf fuer alle Nachbarn ohne angraenzende Mine
     */
    public void aufdecken() {
        if (!istMarkiert) { //damit Zellen die bereits Markiert sind nicht als aufgedeckt deklariert werden
            istAufgedeckt = true;
        }
        if (benachbarteMinen == 0) {
            for (Zelle z : nachbarn) {
                if (!z.istAufgedeckt) { //quasi Rekursions-Endbedingung
                    z.aufdecken();
                }
            }
        }
    }

    public ArrayList<Zelle> getNachbarn() {
        return nachbarn;
    }

    public Minefield getField() {
        return field;
    }
}
