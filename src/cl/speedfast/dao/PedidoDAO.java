package cl.speedfast.dao;

import cl.speedfast.conexion.ConexionDB;
import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Pedido.
 * Implementa operaciones CRUD completas y consultas filtradas sobre la tabla 'pedidos'
 * utilizando PreparedStatement y ResultSet.
 */
public class PedidoDAO {

    /**
     * Registra un nuevo pedido en la base de datos.
     * @param pedido objeto con los datos del pedido a persistir
     * @return true si se insertó con éxito, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pedido.getDireccion().trim());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    pedido.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Lee todos los pedidos registrados en el sistema.
     * @return List de pedidos ordenados por ID de manera ascendente
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Pedido> readAll() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id, direccion, tipo, estado FROM pedidos ORDER BY id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setDireccion(rs.getString("direccion"));
                p.setTipo(TipoPedido.desdeTexto(rs.getString("tipo")));
                p.setEstado(EstadoPedido.desdeTexto(rs.getString("estado")));
                lista.add(p);
            }
            return lista;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Busca un pedido específico por su identificador único.
     * @param id identificador del pedido
     * @return Pedido correspondiente o null si no se encuentra
     * @throws SQLException si ocurre un error en la consulta
     */
    public Pedido readById(int id) throws SQLException {
        String sql = "SELECT id, direccion, tipo, estado FROM pedidos WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setDireccion(rs.getString("direccion"));
                p.setTipo(TipoPedido.desdeTexto(rs.getString("tipo")));
                p.setEstado(EstadoPedido.desdeTexto(rs.getString("estado")));
                return p;
            }
            return null;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Consulta pedidos aplicando filtros opcionales por Tipo y/o Estado.
     * @param tipoFiltro  Tipo de pedido (o null / "TODOS" para omitir)
     * @param estadoFiltro Estado del pedido (o null / "TODOS" para omitir)
     * @return List de pedidos filtrados
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<Pedido> readByFiltros(String tipoFiltro, String estadoFiltro) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, direccion, tipo, estado FROM pedidos WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (tipoFiltro != null && !tipoFiltro.trim().isEmpty() && !tipoFiltro.equalsIgnoreCase("TODOS")) {
            sql.append(" AND tipo = ?");
            parametros.add(tipoFiltro.trim().toUpperCase());
        }

        if (estadoFiltro != null && !estadoFiltro.trim().isEmpty() && !estadoFiltro.equalsIgnoreCase("TODOS")) {
            sql.append(" AND estado = ?");
            parametros.add(estadoFiltro.trim().toUpperCase());
        }

        sql.append(" ORDER BY id ASC");

        List<Pedido> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql.toString());

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setDireccion(rs.getString("direccion"));
                p.setTipo(TipoPedido.desdeTexto(rs.getString("tipo")));
                p.setEstado(EstadoPedido.desdeTexto(rs.getString("estado")));
                lista.add(p);
            }
            return lista;
        } finally {
            ConexionDB.cerrarRecursos(rs, ps, conn);
        }
    }

    /**
     * Actualiza los datos de un pedido existente.
     * @param pedido pedido con la información modificada
     * @return true si la actualización afectó al menos una fila
     * @throws SQLException si ocurre un error en la actualización
     */
    public boolean update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, pedido.getDireccion().trim());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, pedido.getId());

            return ps.executeUpdate() > 0;
        } finally {
            ConexionDB.cerrarRecursos(null, ps, conn);
        }
    }

    /**
     * Elimina un pedido por su identificador.
     * @param id identificador del pedido
     * @return true si fue eliminado correctamente
     * @throws SQLException si ocurre un error en la eliminación o restricción referencial
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
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
