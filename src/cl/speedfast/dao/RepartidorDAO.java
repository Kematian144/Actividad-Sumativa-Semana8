package cl.speedfast.dao;

import cl.speedfast.conexion.ConexionDB;
import cl.speedfast.modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Repartidor.
 * Implementa operaciones CRUD completas sobre la tabla 'repartidores'
 * utilizando PreparedStatement y ResultSet.
 */
public class RepartidorDAO {

    /**
     * Registra un nuevo repartidor en la base de datos.
     * @param repartidor objeto con los datos a registrar
     * @return true si se insertó exitosamente, false en caso contrario
     * @throws SQLException si ocurre un error en la ejecución SQL
     */
    public boolean create(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, repartidor.getNombre().trim());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    repartidor.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Obtiene todos los repartidores ordenados por ID de manera ascendente.
     * @return List de repartidores
     * @throws SQLException si ocurre un error en la ejecución SQL
     */
    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM repartidores ORDER BY id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Repartidor r = new Repartidor();
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                lista.add(r);
            }
            return lista;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Busca un repartidor por su identificador único.
     * @param id identificador del repartidor
     * @return Repartidor encontrado o null si no existe
     * @throws SQLException si ocurre un error en la ejecución SQL
     */
    public Repartidor readById(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM repartidores WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Repartidor(rs.getInt("id"), rs.getString("nombre"));
            }
            return null;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Actualiza los datos de un repartidor existente.
     * @param repartidor objeto con los datos actualizados
     * @return true si se actualizó al menos una fila, false en caso contrario
     * @throws SQLException si ocurre un error en la ejecución SQL
     */
    public boolean update(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, repartidor.getNombre().trim());
            ps.setInt(2, repartidor.getId());

            return ps.executeUpdate() > 0;
        } finally {
            ConexionDB.cerrarRecursos(null, ps, conn);
        }
    }

    /**
     * Elimina un repartidor por su identificador único.
     * @param id identificador del repartidor a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos o violación de clave foránea
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";
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
