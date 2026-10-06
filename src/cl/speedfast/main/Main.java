package cl.speedfast.main;

import cl.speedfast.conexion.ConexionDB;
import cl.speedfast.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal del sistema SpeedFast para la evaluación de la Semana 8.
 * Configura la apariencia del sistema operativo (Look & Feel)
 * e inicia la interfaz gráfica en el hilo de despacho de eventos de Swing (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Establecer apariencia nativa del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }

        // Imprimir estado inicial de conectividad por consola
        System.out.println("==================================================");
        System.out.println("   SPEEDFAST - GESTIÓN DE PEDIDOS Y ENTREGAS     ");
        System.out.println("   Desarrollo Orientado a Objetos II - Semana 8   ");
        System.out.println("==================================================");
        System.out.println("URL JDBC: " + ConexionDB.getUrl());
        boolean dbConectada = ConexionDB.probarConexion();
        if (dbConectada) {
            System.out.println("Estado BD: CONECTADO a MySQL (speedfast_db)");
        } else {
            System.out.println("Estado BD: DESCONECTADO (Inicie el servicio MySQL)");
        }
        System.out.println("Iniciando Interfaz Gráfica Swing...");

        // Iniciar GUI en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
