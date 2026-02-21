public class Ham {

    // Identificador único del jamón
    private final int id;

    // Constructor: crea un jamón con un ID concreto
    public Ham(int id) {
        this.id = id;
    }

    // Devuelve el ID del jamón
    public int getId() {
        return id;
    }

    // Representación en texto del jamón (Para imprimir)
    @Override
    public String toString() {
        return "Jamon-" + id;
    }
}
