package cl.speedfast.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa la asociación y despacho de una Entrega entre un Pedido y un Repartidor.
 * Mapea la tabla 'entregas' de la base de datos MySQL, incluyendo claves foráneas.
 */
public class Entrega {

    private int id;
    private int idPedido;
    private int idRepartidor;
    private LocalDate fecha;
    private LocalTime hora;

    // Campos auxiliares para visualización en tablas y vistas
    private String direccionPedido;
    private String nombreRepartidor;
    private TipoPedido tipoPedido;
    private EstadoPedido estadoPedido;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Constructor vacío
    public Entrega() {
        this.fecha = LocalDate.now();
        this.hora = LocalTime.now();
    }

    // Constructor sin ID (para inserción en BD)
    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = (fecha != null) ? fecha : LocalDate.now();
        this.hora = (hora != null) ? hora : LocalTime.now();
    }

    // Constructor completo con ID
    public Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = (fecha != null) ? fecha : LocalDate.now();
        this.hora = (hora != null) ? hora : LocalTime.now();
    }

    // Constructor con campos auxiliares unidos desde JOIN en BD
    public Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora,
                   String direccionPedido, String nombreRepartidor,
                   TipoPedido tipoPedido, EstadoPedido estadoPedido) {
        this(id, idPedido, idRepartidor, fecha, hora);
        this.direccionPedido = direccionPedido;
        this.nombreRepartidor = nombreRepartidor;
        this.tipoPedido = tipoPedido;
        this.estadoPedido = estadoPedido;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(int idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getDireccionPedido() {
        return direccionPedido;
    }

    public void setDireccionPedido(String direccionPedido) {
        this.direccionPedido = direccionPedido;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }

    public TipoPedido getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(TipoPedido tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public String getFechaFormateada() {
        return (fecha != null) ? fecha.format(DATE_FORMATTER) : "";
    }

    public String getHoraFormateada() {
        return (hora != null) ? hora.format(TIME_FORMATTER) : "";
    }

    @Override
    public String toString() {
        return "Entrega #" + id + " [Pedido #" + idPedido + ", Repartidor #" + idRepartidor
                + " en " + getFechaFormateada() + " " + getHoraFormateada() + "]";
    }
}
