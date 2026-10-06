package cl.speedfast.modelo;

/**
 * Representa la entidad Pedido en el sistema SpeedFast.
 * Mapea la tabla 'pedidos' de la base de datos MySQL con sus campos oficiales:
 * id, direccion, tipo (ENUM) y estado (ENUM).
 */
public class Pedido {

    protected int id;
    protected String direccion;
    protected TipoPedido tipo;
    protected EstadoPedido estado;

    // Constructor vacío
    public Pedido() {
        this.tipo = TipoPedido.COMIDA;
        this.estado = EstadoPedido.PENDIENTE;
    }

    // Constructor sin ID (usado para inserción en BD donde ID es auto-incremental)
    public Pedido(String direccion, TipoPedido tipo, EstadoPedido estado) {
        this.direccion = direccion;
        this.tipo = (tipo != null) ? tipo : TipoPedido.COMIDA;
        this.estado = (estado != null) ? estado : EstadoPedido.PENDIENTE;
    }

    // Constructor completo con ID
    public Pedido(int id, String direccion, TipoPedido tipo, EstadoPedido estado) {
        this.id = id;
        this.direccion = direccion;
        this.tipo = (tipo != null) ? tipo : TipoPedido.COMIDA;
        this.estado = (estado != null) ? estado : EstadoPedido.PENDIENTE;
    }

    // Constructor con cadenas de texto (para compatibilidad)
    public Pedido(int id, String direccion, String tipoStr, String estadoStr) {
        this.id = id;
        this.direccion = direccion;
        this.tipo = TipoPedido.desdeTexto(tipoStr);
        this.estado = EstadoPedido.desdeTexto(estadoStr);
    }

    /**
     * Calcula el tiempo estimado de entrega según el tipo de pedido.
     * @return tiempo en minutos
     */
    public int calcularTiempoEntrega() {
        if (tipo == null) return 30;
        switch (tipo) {
            case COMIDA:
                return 30;
            case ENCOMIENDA:
                return 60;
            case EXPRESS:
                return 15;
            default:
                return 45;
        }
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public void setTipo(TipoPedido tipo) {
        this.tipo = tipo;
    }

    public void setTipo(String tipoStr) {
        this.tipo = TipoPedido.desdeTexto(tipoStr);
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public void setEstado(String estadoStr) {
        this.estado = EstadoPedido.desdeTexto(estadoStr);
    }

    @Override
    public String toString() {
        return "ID: " + id + " - " + direccion + " (" + tipo + " - " + estado + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return id == pedido.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
