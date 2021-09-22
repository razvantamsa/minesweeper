/**
 * serializierbares Parent der Zellen-Klasse
 */
public class SaveZelle {

    protected boolean istMine = false;
    protected boolean istAufgedeckt = false;
    protected boolean istMarkiert = false;
    protected int benachbarteMinen = 0;

    SaveZelle() {}

    /**
     * Neue Instanz aus Zelle Objekt
     * @param zelle
     */
    public SaveZelle(Zelle zelle) {
        this.istMine = zelle.istMine;
        this.istAufgedeckt = zelle.istAufgedeckt;
        this.istMarkiert = zelle.istMarkiert;
        this.benachbarteMinen = zelle.benachbarteMinen;
    }

    public boolean istMine() {
        return istMine;
    }

    public void setIstMine(boolean istMine) {
        this.istMine = istMine;
    }

    public boolean istAufgedeckt() {
        return istAufgedeckt;
    }

    public void setIstAufgedeckt(boolean istAufgedeckt) {
        this.istAufgedeckt = istAufgedeckt;
    }

    public boolean istMarkiert() {
        return istMarkiert;
    }

    public void setIstMarkiert(boolean istMarkiert) {
        this.istMarkiert = istMarkiert;
    }

    public int getBenachbarteMinen() {
        return benachbarteMinen;
    }

    public void setBenachbarteMinen(int benachbarteMinen) {
        this.benachbarteMinen = benachbarteMinen;
    }
}
