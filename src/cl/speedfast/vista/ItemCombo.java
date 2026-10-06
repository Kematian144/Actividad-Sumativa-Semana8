package cl.speedfast.vista;

/**
 * Clase genérica de soporte para componentes JComboBox.
 * Permite presentar una etiqueta amigable y legible para el usuario en la interfaz,
 * preservando internamente el identificador numérico (ID) y el objeto entidad subyacente.
 *
 * @param <T> tipo de entidad que envuelve (p. ej. Repartidor, Pedido, etc.)
 */
public class ItemCombo<T> {

    private final int id;
    private final String etiqueta;
    private final T entidad;

    public ItemCombo(int id, String etiqueta, T entidad) {
        this.id = id;
        this.etiqueta = etiqueta;
        this.entidad = entidad;
    }

    public int getId() {
        return id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public T getEntidad() {
        return entidad;
    }

    @Override
    public String toString() {
        return etiqueta;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemCombo<?> that = (ItemCombo<?>) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
