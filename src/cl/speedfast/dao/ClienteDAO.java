package cl.speedfast.dao;

import cl.speedfast.conexion.ConexionDB;
import cl.speedfast.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Cliente.
 * Cumple con el requerimiento explícito del Paso 2 de la pauta:
 * "Crea una clase ClienteDAO con métodos create(), readAll(), update() y delete() para gestionar clientes."
 * Utiliza PreparedStatement y ResultSet para garantizar acceso seguro y eficiente.
 */
public class ClienteDAO {

    /**
     * Registra un nuevo cliente en la base de datos.
     * @param cliente cliente a registrar
     * @return true si la inserción fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean create(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, email, telefono, direccion) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente.getNombre().trim());
            ps.setString(2, cliente.getEmail().trim());
            ps.setString(3, cliente.getTelefono().trim());
            ps.setString(4, cliente.getDireccion().trim());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Obtiene todos los clientes registrados.
     * @return List de clientes
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<Cliente> readAll() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, email, telefono, direccion FROM clientes ORDER BY id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setEmail(rs.getString("email"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                lista.add(c);
            }
            return lista;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Busca un cliente por su ID.
     * @param id identificador del cliente
     * @return Cliente correspondiente o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     */
    public Cliente readById(int id) throws SQLException {
        String sql = "SELECT id, nombre, email, telefono, direccion FROM clientes WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setEmail(rs.getString("email"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                return c;
            }
            return null;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Actualiza la información de un cliente existente.
     * @param cliente cliente con datos modificados
     * @return true si se actualizó exitosamente
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean update(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ?, direccion = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, cliente.getNombre().trim());
            ps.setString(2, cliente.getEmail().trim());
            ps.setString(3, cliente.getTelefono().trim());
            ps.setString(4, cliente.getDireccion().trim());
            ps.setInt(5, cliente.getId());

            return ps.executeUpdate() > 0;
        } finally {
            ConexionDB.cerrarRecursos(null, ps, conn);
        }
    }

    /**
     * Elimina un cliente por su identificador.
     * @param id identificador del cliente
     * @return true si se eliminó exitosamente
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } finally {
            ConexionDB.cerrarRecursos(null, ps, conn);
        }
    }
}
