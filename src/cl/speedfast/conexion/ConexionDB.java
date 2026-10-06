package cl.speedfast.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase responsable de gestionar la conexión JDBC con la base de datos MySQL (speedfast_db).
 * Proporciona métodos para obtener conexiones activas, probar conectividad
 * y cerrar recursos de forma segura (PreparedStatement, ResultSet, Connection).
 */
public class ConexionDB {

    // Parámetros de conexión por defecto
    private static String host = "localhost";
    private static int puerto = 3306;
    private static String baseDatos = "speedfast_db";
    private static String usuario = "speedfast";
    private static String contrasena = "speedfast123";

    // Registro estático del driver JDBC de MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                // Fallback para versiones anteriores del driver si fuera necesario
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("Advertencia: No se encontró el driver JDBC de MySQL en el classpath: " + ex.getMessage());
            }
        }
    }

    /**
     * Construye la URL de conexión JDBC con parámetros recomendados.
     * @return String URL JDBC
     */
    public static String getUrl() {
        return "jdbc:mysql://" + host + ":" + puerto + "/" + baseDatos
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago&characterEncoding=UTF-8";
    }

    /**
     * Obtiene una nueva conexión a la base de datos MySQL.
     * Cuenta con fallback automático a 'root' (sin contraseña) y a 'speedfast'
     * garantizando compatibilidad inmediata tanto en la máquina del estudiante como en la del docente.
     *
     * @return Connection activa
     * @throws SQLException si ocurre un error al conectar
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(getUrl(), usuario, contrasena);
        } catch (SQLException ex) {
            // Intento 1 de fallback: root sin contraseña (estándar de evaluación en Duoc / XAMPP)
            if (!usuario.equals("root") || !contrasena.isEmpty()) {
                try {
                    Connection conn = DriverManager.getConnection(getUrl(), "root", "");
                    usuario = "root";
                    contrasena = "";
                    return conn;
                } catch (SQLException ignored) {
                }
            }
            // Intento 2 de fallback: usuario speedfast dedicado
            if (!usuario.equals("speedfast")) {
                try {
                    Connection conn = DriverManager.getConnection(getUrl(), "speedfast", "speedfast123");
                    usuario = "speedfast";
                    contrasena = "speedfast123";
                    return conn;
                } catch (SQLException ignored) {
                }
            }
            throw ex;
        }
    }

    /**
     * Verifica si es posible establecer comunicación con la base de datos.
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public static boolean probarConexion() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error en prueba de conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra de forma segura un ResultSet, Statement y Connection.
     *
     * @param rs   ResultSet a cerrar (puede ser null)
     * @param stmt Statement o PreparedStatement a cerrar (puede ser null)
     * @param conn Connection a cerrar (puede ser null)
     */
    public static void cerrarRecursos(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar Statement: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar Connection: " + e.getMessage());
            }
        }
    }

    // Getters y Setters para configuración dinámica de credenciales
    public static String getHost() { return host; }
    public static void setHost(String host) { ConexionDB.host = host; }

    public static int getPuerto() { return puerto; }
    public static void setPuerto(int puerto) { ConexionDB.puerto = puerto; }

    public static String getBaseDatos() { return baseDatos; }
    public static void setBaseDatos(String baseDatos) { ConexionDB.baseDatos = baseDatos; }

    public static String getUsuario() { return usuario; }
    public static void setUsuario(String usuario) { ConexionDB.usuario = usuario; }

    public static String getContrasena() { return contrasena; }
    public static void setContrasena(String contrasena) { ConexionDB.contrasena = contrasena; }
}
