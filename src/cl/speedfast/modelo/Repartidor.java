package cl.speedfast.modelo;

/**
 * Representa a un repartidor registrado en el sistema SpeedFast.
 * Mapea la tabla 'repartidores' de la base de datos MySQL.
 */
public class Repartidor {

    private int id;
    private String nombre;

    // Constructor vacío
    public Repartidor() {
    }

    // Constructor sin ID (usado para inserción en BD donde ID es auto-incremental)
    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    // Constructor completo con ID
    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "ID: " + id + " - " + nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repartidor that = (Repartidor) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
