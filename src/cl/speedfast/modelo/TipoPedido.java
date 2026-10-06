package cl.speedfast.modelo;

/**
 * Enumerador que representa los tipos de pedidos admitidos en SpeedFast.
 * Coincide con el tipo ENUM('COMIDA','ENCOMIENDA','EXPRESS') en la base de datos.
 */
public enum TipoPedido {
    COMIDA("Comida"),
    ENCOMIENDA("Encomienda"),
    EXPRESS("Express");

    private final String etiqueta;

    TipoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }

    /**
     * Convierte una cadena de texto a su correspondiente TipoPedido.
     * @param texto nombre del enum o etiqueta
     * @return TipoPedido correspondiente o COMIDA por defecto
     */
    public static TipoPedido desdeTexto(String texto) {
        if (texto == null) return COMIDA;
        for (TipoPedido tp : values()) {
            if (tp.name().equalsIgnoreCase(texto.trim()) || tp.etiqueta.equalsIgnoreCase(texto.trim())) {
                return tp;
            }
        }
        return COMIDA;
    }
}
