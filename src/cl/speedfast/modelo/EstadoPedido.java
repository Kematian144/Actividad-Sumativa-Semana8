package cl.speedfast.modelo;

/**
 * Enumerador que representa los estados posibles de un pedido en SpeedFast.
 * Coincide con el tipo ENUM('PENDIENTE','EN_REPARTO','ENTREGADO') en la base de datos.
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    EN_REPARTO("En Reparto"),
    ENTREGADO("Entregado");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
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
     * Convierte una cadena de texto a su correspondiente EstadoPedido.
     * @param texto nombre del enum o etiqueta
     * @return EstadoPedido correspondiente o PENDIENTE por defecto
     */
    public static EstadoPedido desdeTexto(String texto) {
        if (texto == null) return PENDIENTE;
        for (EstadoPedido ep : values()) {
            if (ep.name().equalsIgnoreCase(texto.trim()) || ep.etiqueta.equalsIgnoreCase(texto.trim())) {
                return ep;
            }
        }
        return PENDIENTE;
    }
}
