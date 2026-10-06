package cl.speedfast.dao;

import cl.speedfast.conexion.ConexionDB;
import cl.speedfast.modelo.Entrega;
import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.TipoPedido;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Entrega.
 * Gestiona la persistencia y relaciones (claves foráneas) entre Pedidos y Repartidores
 * utilizando PreparedStatement y ResultSet.
 */
public class EntregaDAO {

    /**
     * Registra una nueva entrega en la base de datos asociando pedido y repartidor con fecha y hora.
     * @param entrega entrega con los identificadores foráneos y marcas de tiempo
     * @return true si la inserción fue exitosa
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));

            int filas = ps.executeUpdate();
            if (filas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entrega.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Obtiene todas las entregas registradas realizando un JOIN con pedidos y repartidores
     * para facilitar la visualización de nombres y direcciones en la interfaz de usuario.
     * @return List de entregas
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<Entrega> readAll() throws SQLException {
        String sql = "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
                   + "       p.direccion AS dir_pedido, p.tipo AS tipo_pedido, p.estado AS estado_pedido, "
                   + "       r.nombre AS nom_repartidor "
                   + "FROM entregas e "
                   + "INNER JOIN pedidos p ON e.id_pedido = p.id "
                   + "INNER JOIN repartidores r ON e.id_repartidor = r.id "
                   + "ORDER BY e.id ASC";

        List<Entrega> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearEntregaConJoin(rs));
            }
            return lista;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Busca una entrega por su identificador único.
     * @param id identificador de la entrega
     * @return Entrega correspondiente o null
     * @throws SQLException si ocurre un error en la consulta
     */
    public Entrega readById(int id) throws SQLException {
        String sql = "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
                   + "       p.direccion AS dir_pedido, p.tipo AS tipo_pedido, p.estado AS estado_pedido, "
                   + "       r.nombre AS nom_repartidor "
                   + "FROM entregas e "
                   + "INNER JOIN pedidos p ON e.id_pedido = p.id "
                   + "INNER JOIN repartidores r ON e.id_repartidor = r.id "
                   + "WHERE e.id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapearEntregaConJoin(rs);
            }
            return null;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Filtra entregas asignadas a un repartidor específico.
     * @param idRepartidor identificador del repartidor
     * @return List de entregas asignadas al repartidor
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<Entrega> readByRepartidor(int idRepartidor) throws SQLException {
        String sql = "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
                   + "       p.direccion AS dir_pedido, p.tipo AS tipo_pedido, p.estado AS estado_pedido, "
                   + "       r.nombre AS nom_repartidor "
                   + "FROM entregas e "
                   + "INNER JOIN pedidos p ON e.id_pedido = p.id "
                   + "INNER JOIN repartidores r ON e.id_repartidor = r.id "
                   + "WHERE e.id_repartidor = ? "
                   + "ORDER BY e.id ASC";

        List<Entrega> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idRepartidor);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearEntregaConJoin(rs));
            }
            return lista;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Filtra entregas asociadas a un pedido específico.
     * @param idPedido identificador del pedido
     * @return List de entregas asociadas
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<Entrega> readByPedido(int idPedido) throws SQLException {
        String sql = "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
                   + "       p.direccion AS dir_pedido, p.tipo AS tipo_pedido, p.estado AS estado_pedido, "
                   + "       r.nombre AS nom_repartidor "
                   + "FROM entregas e "
                   + "INNER JOIN pedidos p ON e.id_pedido = p.id "
                   + "INNER JOIN repartidores r ON e.id_repartidor = r.id "
                   + "WHERE e.id_pedido = ? "
                   + "ORDER BY e.id ASC";

        List<Entrega> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idPedido);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearEntregaConJoin(rs));
            }
            return lista;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Actualiza los datos de una entrega existente (pedido, repartidor, fecha, hora).
     * @param entrega objeto con los datos modificados
     * @return true si se actualizó con éxito
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.setInt(5, entrega.getId());

            return ps.executeUpdate() > 0;
        } finally {
            ConexionDB.cerrarRecursos(null, ps, conn);
        }
    }

    /**
     * Elimina una entrega por su identificador.
     * @param id identificador de la entrega
     * @return true si fue eliminada
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";
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

    /**
     * Método auxiliar privado para construir un objeto Entrega a partir de un ResultSet con JOIN.
     */
    private Entrega mapearEntregaConJoin(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int idPedido = rs.getInt("id_pedido");
        int idRepartidor = rs.getInt("id_repartidor");

        Date sqlDate = rs.getDate("fecha");
        LocalDate fecha = (sqlDate != null) ? sqlDate.toLocalDate() : LocalDate.now();

        Time sqlTime = rs.getTime("hora");
        LocalTime hora = (sqlTime != null) ? sqlTime.toLocalTime() : LocalTime.now();

        String dirPedido = rs.getString("dir_pedido");
        String nomRepartidor = rs.getString("nom_repartidor");
        TipoPedido tipoPedido = TipoPedido.desdeTexto(rs.getString("tipo_pedido"));
        EstadoPedido estadoPedido = EstadoPedido.desdeTexto(rs.getString("estado_pedido"));

        return new Entrega(id, idPedido, idRepartidor, fecha, hora, dirPedido, nomRepartidor, tipoPedido, estadoPedido);
    }
}
