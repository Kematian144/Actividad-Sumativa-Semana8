package cl.speedfast.vista;

import cl.speedfast.conexion.ConexionDB;
import cl.speedfast.dao.ClienteDAO;
import cl.speedfast.dao.EntregaDAO;
import cl.speedfast.dao.PedidoDAO;
import cl.speedfast.dao.RepartidorDAO;
import cl.speedfast.modelo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Ventana principal de la aplicación SpeedFast desarrollada en Java Swing.
 * Integra la interfaz gráfica con las clases DAO para operaciones CRUD en tiempo real,
 * validación rigurosa de entradas, manejo de excepciones SQL con mensajes informativos
 * y componentes gráficos completos (JTable, JComboBox, JTextField, JButton, JTabbedPane).
 */
public class VentanaPrincipal extends JFrame {

    // DAOs
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // Pestañas
    private JTabbedPane tabbedPane;

    // Componentes - Pestaña Repartidores
    private JTextField txtIdRepartidor;
    private JTextField txtNombreRepartidor;
    private JButton btnRegistrarRepartidor;
    private JButton btnActualizarRepartidor;
    private JButton btnEliminarRepartidor;
    private JButton btnLimpiarRepartidor;
    private JTable tablaRepartidores;
    private DefaultTableModel modeloRepartidores;

    // Componentes - Pestaña Pedidos
    private JTextField txtIdPedido;
    private JTextField txtDireccionPedido;
    private JComboBox<TipoPedido> cbTipoPedido;
    private JComboBox<EstadoPedido> cbEstadoPedido;
    private JButton btnRegistrarPedido;
    private JButton btnActualizarPedido;
    private JButton btnEliminarPedido;
    private JButton btnLimpiarPedido;
    private JComboBox<String> cbFiltroTipoPedido;
    private JComboBox<String> cbFiltroEstadoPedido;
    private JButton btnFiltrarPedidos;
    private JButton btnRestablecerFiltrosPedidos;
    private JTable tablaPedidos;
    private DefaultTableModel modeloPedidos;

    // Componentes - Pestaña Entregas
    private JTextField txtIdEntrega;
    private JComboBox<ItemCombo<Pedido>> cbPedidoEntrega;
    private JComboBox<ItemCombo<Repartidor>> cbRepartidorEntrega;
    private JTextField txtFechaEntrega;
    private JTextField txtHoraEntrega;
    private JButton btnRegistrarEntrega;
    private JButton btnActualizarEntrega;
    private JButton btnEliminarEntrega;
    private JButton btnLimpiarEntrega;
    private JComboBox<ItemCombo<Repartidor>> cbFiltroRepartidorEntrega;
    private JComboBox<ItemCombo<Pedido>> cbFiltroPedidoEntrega;
    private JButton btnFiltrarEntregas;
    private JButton btnRestablecerFiltrosEntregas;
    private JTable tablaEntregas;
    private DefaultTableModel modeloEntregas;

    // Componentes - Pestaña Clientes
    private JTextField txtIdCliente;
    private JTextField txtNombreCliente;
    private JTextField txtEmailCliente;
    private JTextField txtTelefonoCliente;
    private JTextField txtDireccionCliente;
    private JButton btnRegistrarCliente;
    private JButton btnActualizarCliente;
    private JButton btnEliminarCliente;
    private JButton btnLimpiarCliente;
    private JTable tablaClientes;
    private DefaultTableModel modeloClientes;

    // Barra de estado inferior
    private JLabel lblMensajeEstado;

    // Paleta de colores para botones y acciones
    private static final Color COLOR_REGISTRAR = new Color(40, 167, 69);
    private static final Color COLOR_REGISTRAR_HOVER = new Color(46, 184, 77);
    private static final Color COLOR_REGISTRAR_CLICK = new Color(30, 126, 52);

    private static final Color COLOR_ACTUALIZAR = new Color(13, 110, 253);
    private static final Color COLOR_ACTUALIZAR_HOVER = new Color(31, 128, 255);
    private static final Color COLOR_ACTUALIZAR_CLICK = new Color(11, 94, 215);

    private static final Color COLOR_ELIMINAR = new Color(220, 53, 69);
    private static final Color COLOR_ELIMINAR_HOVER = new Color(230, 75, 90);
    private static final Color COLOR_ELIMINAR_CLICK = new Color(187, 45, 59);

    private static final Color COLOR_LIMPIAR = new Color(108, 117, 125);
    private static final Color COLOR_LIMPIAR_HOVER = new Color(125, 135, 144);
    private static final Color COLOR_LIMPIAR_CLICK = new Color(90, 98, 104);

    /**
     * Crea un botón moderno con fondo plano antialiasing y texto blanco de alto contraste.
     * Es 100% compatible con Windows L&F y garantiza que el texto nunca quede invisible.
     */
    private JButton crearBotonAccion(String texto, Color bgNormal, Color bgHover, Color bgClick, Color fgColor) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgClick);
                } else if (getModel().isRollover()) {
                    g2.setColor(bgHover);
                } else {
                    g2.setColor(bgNormal);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Crea un botón secundario plano elegante para filtros y utilidades.
     */
    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(210, 214, 218));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(230, 233, 236));
                } else {
                    g2.setColor(new Color(245, 246, 248));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(205, 210, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setForeground(new Color(40, 45, 50));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(85, 26));
        return btn;
    }

    public VentanaPrincipal() {
        setTitle("SpeedFast - Sistema de Gestión de Pedidos y Entregas (Semana 8)");
        setSize(1100, 720);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializarComponentes();
        verificarYRecargarTodo();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // 1. Encabezado superior
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(new Color(25, 45, 80));
        panelEncabezado.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitulo = new JLabel("SpeedFast - Gestión Operacional y Logística");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Persistencia JDBC • MySQL • Arquitectura MVC/DAO • Duoc UC");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(200, 220, 245));

        panelEncabezado.add(lblTitulo, BorderLayout.NORTH);
        panelEncabezado.add(lblSubtitulo, BorderLayout.SOUTH);
        add(panelEncabezado, BorderLayout.NORTH);

        // 2. Panel central con pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Pedidos", crearPanelPedidos());
        tabbedPane.addTab("Repartidores", crearPanelRepartidores());
        tabbedPane.addTab("Entregas", crearPanelEntregas());
        tabbedPane.addTab("Clientes", crearPanelClientes());

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Barra de estado inferior
        JPanel panelEstado = new JPanel(new BorderLayout());
        panelEstado.setBorder(new EmptyBorder(6, 15, 6, 15));
        panelEstado.setBackground(new Color(245, 245, 245));

        lblMensajeEstado = new JLabel("Listo.");
        lblMensajeEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelEstado.add(lblMensajeEstado, BorderLayout.WEST);

        JButton btnRecargarGlobal = crearBotonSecundario("Recargar Todo");
        btnRecargarGlobal.setToolTipText("Actualiza todas las tablas y listas desplegables desde la BD");
        btnRecargarGlobal.addActionListener(e -> verificarYRecargarTodo());
        panelEstado.add(btnRecargarGlobal, BorderLayout.EAST);

        add(panelEstado, BorderLayout.SOUTH);
    }

    // =========================================================================
    // 1. PESTAÑA PEDIDOS
    // =========================================================================
    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Formulario izquierdo
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Datos del Pedido", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        panelForm.setPreferredSize(new Dimension(360, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        txtIdPedido = new JTextField();
        txtIdPedido.setEditable(false);
        txtIdPedido.setText("Auto (Nuevo)");
        txtIdPedido.setBackground(new Color(240, 240, 240));

        txtDireccionPedido = new JTextField(20);
        cbTipoPedido = new JComboBox<>(TipoPedido.values());
        cbEstadoPedido = new JComboBox<>(EstadoPedido.values());

        panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panelForm.add(txtIdPedido, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Dirección: *"), gbc);
        gbc.gridx = 1; panelForm.add(txtDireccionPedido, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; panelForm.add(cbTipoPedido, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; panelForm.add(cbEstadoPedido, gbc);

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBotones.setOpaque(false);
        btnRegistrarPedido = crearBotonAccion("Registrar", COLOR_REGISTRAR, COLOR_REGISTRAR_HOVER, COLOR_REGISTRAR_CLICK, Color.WHITE);
        btnActualizarPedido = crearBotonAccion("Actualizar", COLOR_ACTUALIZAR, COLOR_ACTUALIZAR_HOVER, COLOR_ACTUALIZAR_CLICK, Color.WHITE);
        btnEliminarPedido = crearBotonAccion("Eliminar", COLOR_ELIMINAR, COLOR_ELIMINAR_HOVER, COLOR_ELIMINAR_CLICK, Color.WHITE);
        btnLimpiarPedido = crearBotonAccion("Limpiar", COLOR_LIMPIAR, COLOR_LIMPIAR_HOVER, COLOR_LIMPIAR_CLICK, Color.WHITE);

        panelBotones.add(btnRegistrarPedido);
        panelBotones.add(btnActualizarPedido);
        panelBotones.add(btnEliminarPedido);
        panelBotones.add(btnLimpiarPedido);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 6, 8);
        panelForm.add(panelBotones, gbc);

        panel.add(panelForm, BorderLayout.WEST);

        // Centro: Filtros + Tabla
        JPanel panelTablaContenedor = new JPanel(new BorderLayout(8, 8));

        // Panel de Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));
        panelFiltros.setPreferredSize(new Dimension(0, 58));

        cbFiltroTipoPedido = new JComboBox<>(new String[]{"TODOS", "COMIDA", "ENCOMIENDA", "EXPRESS"});
        cbFiltroTipoPedido.setPreferredSize(new Dimension(115, 26));
        cbFiltroEstadoPedido = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "EN_REPARTO", "ENTREGADO"});
        cbFiltroEstadoPedido.setPreferredSize(new Dimension(115, 26));
        btnFiltrarPedidos = crearBotonSecundario("Filtrar");
        btnRestablecerFiltrosPedidos = crearBotonSecundario("Restablecer");
        btnRestablecerFiltrosPedidos.setPreferredSize(new Dimension(95, 26));

        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(cbFiltroTipoPedido);
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(cbFiltroEstadoPedido);
        panelFiltros.add(btnFiltrarPedidos);
        panelFiltros.add(btnRestablecerFiltrosPedidos);

        panelTablaContenedor.add(panelFiltros, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Dirección", "Tipo", "Estado", "Tiempo Est. (min)"};
        modeloPedidos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPedidos = new JTable(modeloPedidos);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.setRowHeight(24);
        tablaPedidos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panelTablaContenedor.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);
        panel.add(panelTablaContenedor, BorderLayout.CENTER);

        // Eventos
        btnRegistrarPedido.addActionListener(e -> registrarPedido());
        btnActualizarPedido.addActionListener(e -> actualizarPedido());
        btnEliminarPedido.addActionListener(e -> eliminarPedido());
        btnLimpiarPedido.addActionListener(e -> limpiarFormularioPedido());

        btnFiltrarPedidos.addActionListener(e -> filtrarPedidos());
        btnRestablecerFiltrosPedidos.addActionListener(e -> {
            cbFiltroTipoPedido.setSelectedIndex(0);
            cbFiltroEstadoPedido.setSelectedIndex(0);
            cargarTablaPedidos(null, null);
        });

        tablaPedidos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaPedidos.getSelectedRow();
                if (fila >= 0) {
                    txtIdPedido.setText(modeloPedidos.getValueAt(fila, 0).toString());
                    txtDireccionPedido.setText(modeloPedidos.getValueAt(fila, 1).toString());
                    String tipoStr = modeloPedidos.getValueAt(fila, 2).toString();
                    String estadoStr = modeloPedidos.getValueAt(fila, 3).toString();
                    cbTipoPedido.setSelectedItem(TipoPedido.desdeTexto(tipoStr));
                    cbEstadoPedido.setSelectedItem(EstadoPedido.desdeTexto(estadoStr));
                }
            }
        });

        return panel;
    }

    // =========================================================================
    // 2. PESTAÑA REPARTIDORES
    // =========================================================================
    private JPanel crearPanelRepartidores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Formulario izquierdo
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Datos del Repartidor", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        panelForm.setPreferredSize(new Dimension(360, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        txtIdRepartidor = new JTextField();
        txtIdRepartidor.setEditable(false);
        txtIdRepartidor.setText("Auto (Nuevo)");
        txtIdRepartidor.setBackground(new Color(240, 240, 240));

        txtNombreRepartidor = new JTextField(20);

        panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panelForm.add(txtIdRepartidor, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nombre Completo: *"), gbc);
        gbc.gridx = 1; panelForm.add(txtNombreRepartidor, gbc);

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBotones.setOpaque(false);
        btnRegistrarRepartidor = crearBotonAccion("Registrar", COLOR_REGISTRAR, COLOR_REGISTRAR_HOVER, COLOR_REGISTRAR_CLICK, Color.WHITE);
        btnActualizarRepartidor = crearBotonAccion("Actualizar", COLOR_ACTUALIZAR, COLOR_ACTUALIZAR_HOVER, COLOR_ACTUALIZAR_CLICK, Color.WHITE);
        btnEliminarRepartidor = crearBotonAccion("Eliminar", COLOR_ELIMINAR, COLOR_ELIMINAR_HOVER, COLOR_ELIMINAR_CLICK, Color.WHITE);
        btnLimpiarRepartidor = crearBotonAccion("Limpiar", COLOR_LIMPIAR, COLOR_LIMPIAR_HOVER, COLOR_LIMPIAR_CLICK, Color.WHITE);

        panelBotones.add(btnRegistrarRepartidor);
        panelBotones.add(btnActualizarRepartidor);
        panelBotones.add(btnEliminarRepartidor);
        panelBotones.add(btnLimpiarRepartidor);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 6, 8);
        panelForm.add(panelBotones, gbc);

        panel.add(panelForm, BorderLayout.WEST);

        // Tabla central
        String[] columnas = {"ID", "Nombre del Repartidor"};
        modeloRepartidores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRepartidores = new JTable(modeloRepartidores);
        tablaRepartidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRepartidores.setRowHeight(24);
        tablaRepartidores.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tablaRepartidores), BorderLayout.CENTER);

        // Eventos
        btnRegistrarRepartidor.addActionListener(e -> registrarRepartidor());
        btnActualizarRepartidor.addActionListener(e -> actualizarRepartidor());
        btnEliminarRepartidor.addActionListener(e -> eliminarRepartidor());
        btnLimpiarRepartidor.addActionListener(e -> limpiarFormularioRepartidor());

        tablaRepartidores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaRepartidores.getSelectedRow();
                if (fila >= 0) {
                    txtIdRepartidor.setText(modeloRepartidores.getValueAt(fila, 0).toString());
                    txtNombreRepartidor.setText(modeloRepartidores.getValueAt(fila, 1).toString());
                }
            }
        });

        return panel;
    }

    // =========================================================================
    // 3. PESTAÑA ENTREGAS
    // =========================================================================
    private JPanel crearPanelEntregas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Formulario izquierdo
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Asignación de Entrega", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        panelForm.setPreferredSize(new Dimension(420, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        txtIdEntrega = new JTextField();
        txtIdEntrega.setEditable(false);
        txtIdEntrega.setText("Auto (Nuevo)");
        txtIdEntrega.setBackground(new Color(240, 240, 240));

        cbPedidoEntrega = new JComboBox<>();
        cbRepartidorEntrega = new JComboBox<>();

        txtFechaEntrega = new JTextField(LocalDate.now().toString(), 10);
        txtHoraEntrega = new JTextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 8);

        panelForm.add(new JLabel("ID Entrega:"), gbc);
        gbc.gridx = 1; panelForm.add(txtIdEntrega, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Pedido: *"), gbc);
        gbc.gridx = 1; panelForm.add(cbPedidoEntrega, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Repartidor: *"), gbc);
        gbc.gridx = 1; panelForm.add(cbRepartidorEntrega, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Fecha (YYYY-MM-DD): *"), gbc);
        gbc.gridx = 1; panelForm.add(txtFechaEntrega, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Hora (HH:MM:SS): *"), gbc);
        gbc.gridx = 1; panelForm.add(txtHoraEntrega, gbc);

        // Botón auxiliar para actualizar fecha/hora actual
        JButton btnHoraActual = crearBotonSecundario("Usar Fecha/Hora Actual");
        btnHoraActual.addActionListener(e -> {
            txtFechaEntrega.setText(LocalDate.now().toString());
            txtHoraEntrega.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        panelForm.add(btnHoraActual, gbc);

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBotones.setOpaque(false);
        btnRegistrarEntrega = crearBotonAccion("Registrar", COLOR_REGISTRAR, COLOR_REGISTRAR_HOVER, COLOR_REGISTRAR_CLICK, Color.WHITE);
        btnActualizarEntrega = crearBotonAccion("Actualizar", COLOR_ACTUALIZAR, COLOR_ACTUALIZAR_HOVER, COLOR_ACTUALIZAR_CLICK, Color.WHITE);
        btnEliminarEntrega = crearBotonAccion("Eliminar", COLOR_ELIMINAR, COLOR_ELIMINAR_HOVER, COLOR_ELIMINAR_CLICK, Color.WHITE);
        btnLimpiarEntrega = crearBotonAccion("Limpiar", COLOR_LIMPIAR, COLOR_LIMPIAR_HOVER, COLOR_LIMPIAR_CLICK, Color.WHITE);

        panelBotones.add(btnRegistrarEntrega);
        panelBotones.add(btnActualizarEntrega);
        panelBotones.add(btnEliminarEntrega);
        panelBotones.add(btnLimpiarEntrega);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 6, 6, 6);
        panelForm.add(panelBotones, gbc);

        panel.add(panelForm, BorderLayout.WEST);

        // Centro: Filtros + Tabla de entregas
        JPanel panelTablaContenedor = new JPanel(new BorderLayout(8, 8));

        // Panel de Filtros para entregas
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtro de Entregas"));
        panelFiltros.setPreferredSize(new Dimension(0, 58));

        cbFiltroRepartidorEntrega = new JComboBox<>();
        cbFiltroRepartidorEntrega.setPreferredSize(new Dimension(140, 26));
        cbFiltroPedidoEntrega = new JComboBox<>();
        cbFiltroPedidoEntrega.setPreferredSize(new Dimension(150, 26));
        btnFiltrarEntregas = crearBotonSecundario("Filtrar");
        btnRestablecerFiltrosEntregas = crearBotonSecundario("Restablecer");
        btnRestablecerFiltrosEntregas.setPreferredSize(new Dimension(95, 26));

        panelFiltros.add(new JLabel("Repartidor:"));
        panelFiltros.add(cbFiltroRepartidorEntrega);
        panelFiltros.add(new JLabel("Pedido:"));
        panelFiltros.add(cbFiltroPedidoEntrega);
        panelFiltros.add(btnFiltrarEntregas);
        panelFiltros.add(btnRestablecerFiltrosEntregas);

        panelTablaContenedor.add(panelFiltros, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID Entrega", "ID Pedido", "Dirección Pedido", "ID Repartidor", "Repartidor", "Fecha", "Hora"};
        modeloEntregas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaEntregas = new JTable(modeloEntregas);
        tablaEntregas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEntregas.setRowHeight(24);
        tablaEntregas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panelTablaContenedor.add(new JScrollPane(tablaEntregas), BorderLayout.CENTER);
        panel.add(panelTablaContenedor, BorderLayout.CENTER);

        // Eventos
        btnRegistrarEntrega.addActionListener(e -> registrarEntrega());
        btnActualizarEntrega.addActionListener(e -> actualizarEntrega());
        btnEliminarEntrega.addActionListener(e -> eliminarEntrega());
        btnLimpiarEntrega.addActionListener(e -> limpiarFormularioEntrega());

        btnFiltrarEntregas.addActionListener(e -> filtrarEntregas());
        btnRestablecerFiltrosEntregas.addActionListener(e -> {
            if (cbFiltroRepartidorEntrega.getItemCount() > 0) cbFiltroRepartidorEntrega.setSelectedIndex(0);
            if (cbFiltroPedidoEntrega.getItemCount() > 0) cbFiltroPedidoEntrega.setSelectedIndex(0);
            cargarTablaEntregas();
        });

        tablaEntregas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaEntregas.getSelectedRow();
                if (fila >= 0) {
                    txtIdEntrega.setText(modeloEntregas.getValueAt(fila, 0).toString());
                    int idPed = Integer.parseInt(modeloEntregas.getValueAt(fila, 1).toString());
                    int idRep = Integer.parseInt(modeloEntregas.getValueAt(fila, 3).toString());
                    txtFechaEntrega.setText(modeloEntregas.getValueAt(fila, 5).toString());
                    txtHoraEntrega.setText(modeloEntregas.getValueAt(fila, 6).toString());

                    seleccionarComboPorId(cbPedidoEntrega, idPed);
                    seleccionarComboPorId(cbRepartidorEntrega, idRep);
                }
            }
        });

        return panel;
    }

    // =========================================================================
    // 4. PESTAÑA CLIENTES (Cumple requisito explícito ClienteDAO)
    // =========================================================================
    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Formulario izquierdo
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Datos del Cliente (ClienteDAO)", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        panelForm.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        txtIdCliente = new JTextField();
        txtIdCliente.setEditable(false);
        txtIdCliente.setText("Auto (Nuevo)");
        txtIdCliente.setBackground(new Color(240, 240, 240));

        txtNombreCliente = new JTextField(18);
        txtEmailCliente = new JTextField(18);
        txtTelefonoCliente = new JTextField(18);
        txtDireccionCliente = new JTextField(18);

        panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panelForm.add(txtIdCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nombre: *"), gbc);
        gbc.gridx = 1; panelForm.add(txtNombreCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Email: *"), gbc);
        gbc.gridx = 1; panelForm.add(txtEmailCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; panelForm.add(txtTelefonoCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; panelForm.add(txtDireccionCliente, gbc);

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBotones.setOpaque(false);
        btnRegistrarCliente = crearBotonAccion("Registrar", COLOR_REGISTRAR, COLOR_REGISTRAR_HOVER, COLOR_REGISTRAR_CLICK, Color.WHITE);
        btnActualizarCliente = crearBotonAccion("Actualizar", COLOR_ACTUALIZAR, COLOR_ACTUALIZAR_HOVER, COLOR_ACTUALIZAR_CLICK, Color.WHITE);
        btnEliminarCliente = crearBotonAccion("Eliminar", COLOR_ELIMINAR, COLOR_ELIMINAR_HOVER, COLOR_ELIMINAR_CLICK, Color.WHITE);
        btnLimpiarCliente = crearBotonAccion("Limpiar", COLOR_LIMPIAR, COLOR_LIMPIAR_HOVER, COLOR_LIMPIAR_CLICK, Color.WHITE);

        panelBotones.add(btnRegistrarCliente);
        panelBotones.add(btnActualizarCliente);
        panelBotones.add(btnEliminarCliente);
        panelBotones.add(btnLimpiarCliente);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 6, 6, 6);
        panelForm.add(panelBotones, gbc);

        panel.add(panelForm, BorderLayout.WEST);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Email", "Teléfono", "Dirección"};
        modeloClientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(24);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        // Eventos
        btnRegistrarCliente.addActionListener(e -> registrarCliente());
        btnActualizarCliente.addActionListener(e -> actualizarCliente());
        btnEliminarCliente.addActionListener(e -> eliminarCliente());
        btnLimpiarCliente.addActionListener(e -> limpiarFormularioCliente());

        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    txtIdCliente.setText(modeloClientes.getValueAt(fila, 0).toString());
                    txtNombreCliente.setText(modeloClientes.getValueAt(fila, 1).toString());
                    txtEmailCliente.setText(modeloClientes.getValueAt(fila, 2).toString());
                    txtTelefonoCliente.setText(modeloClientes.getValueAt(fila, 3).toString());
                    txtDireccionCliente.setText(modeloClientes.getValueAt(fila, 4).toString());
                }
            }
        });

        return panel;
    }



    // =========================================================================
    // LÓGICA CRUD - REPARTIDORES
    // =========================================================================
    private void registrarRepartidor() {
        String nombre = txtNombreRepartidor.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlertaValidacion("El nombre del repartidor es obligatorio.", txtNombreRepartidor);
            return;
        }
        if (nombre.length() > 100) {
            mostrarAlertaValidacion("El nombre del repartidor no puede superar los 100 caracteres.", txtNombreRepartidor);
            return;
        }

        try {
            Repartidor r = new Repartidor(nombre);
            boolean exito = repartidorDAO.create(r);
            if (exito) {
                mostrarExito("Repartidor registrado exitosamente con ID #" + r.getId());
                limpiarFormularioRepartidor();
                cargarTablaRepartidores();
                refrescarCombosEntregas();
            } else {
                mostrarError("No se pudo registrar el repartidor en la base de datos.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al registrar repartidor", e);
        }
    }

    private void actualizarRepartidor() {
        String idStr = txtIdRepartidor.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione un repartidor de la tabla para actualizar.", tablaRepartidores);
            return;
        }

        String nombre = txtNombreRepartidor.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlertaValidacion("El nombre del repartidor es obligatorio.", txtNombreRepartidor);
            return;
        }
        if (nombre.length() > 100) {
            mostrarAlertaValidacion("El nombre del repartidor no puede superar los 100 caracteres.", txtNombreRepartidor);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Repartidor r = new Repartidor(id, nombre);
            boolean exito = repartidorDAO.update(r);
            if (exito) {
                mostrarExito("Repartidor #" + id + " actualizado correctamente.");
                limpiarFormularioRepartidor();
                cargarTablaRepartidores();
                refrescarCombosEntregas();
                cargarTablaEntregas();
            } else {
                mostrarError("No se encontró el repartidor para actualizar.");
            }
        } catch (NumberFormatException e) {
            mostrarError("ID de repartidor inválido.");
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al actualizar repartidor", e);
        }
    }

    private void eliminarRepartidor() {
        String idStr = txtIdRepartidor.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione un repartidor de la tabla para eliminar.", tablaRepartidores);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar al repartidor ID #" + idStr + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(idStr);
            boolean exito = repartidorDAO.delete(id);
            if (exito) {
                mostrarExito("Repartidor #" + id + " eliminado correctamente.");
                limpiarFormularioRepartidor();
                cargarTablaRepartidores();
                refrescarCombosEntregas();
                cargarTablaEntregas();
            } else {
                mostrarError("No se pudo eliminar el repartidor.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            mostrarError("No se puede eliminar el repartidor porque tiene entregas asociadas.\nElimine primero las entregas asociadas.");
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al eliminar repartidor", e);
        }
    }

    private void limpiarFormularioRepartidor() {
        txtIdRepartidor.setText("Auto (Nuevo)");
        txtNombreRepartidor.setText("");
        tablaRepartidores.clearSelection();
    }

    private void cargarTablaRepartidores() {
        modeloRepartidores.setRowCount(0);
        try {
            List<Repartidor> lista = repartidorDAO.readAll();
            for (Repartidor r : lista) {
                modeloRepartidores.addRow(new Object[]{r.getId(), r.getNombre()});
            }
            lblMensajeEstado.setText("Repartidores cargados: " + lista.size());
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al cargar lista de repartidores", e);
        }
    }

    // =========================================================================
    // LÓGICA CRUD - PEDIDOS
    // =========================================================================
    private void registrarPedido() {
        String direccion = txtDireccionPedido.getText().trim();
        if (direccion.isEmpty()) {
            mostrarAlertaValidacion("La dirección del pedido es obligatoria.", txtDireccionPedido);
            return;
        }
        if (direccion.length() > 100) {
            mostrarAlertaValidacion("La dirección del pedido no puede superar los 100 caracteres.", txtDireccionPedido);
            return;
        }

        TipoPedido tipo = (TipoPedido) cbTipoPedido.getSelectedItem();
        EstadoPedido estado = (EstadoPedido) cbEstadoPedido.getSelectedItem();

        try {
            Pedido pedido = new Pedido(direccion, tipo, estado);
            boolean exito = pedidoDAO.create(pedido);
            if (exito) {
                mostrarExito("Pedido registrado exitosamente con ID #" + pedido.getId());
                limpiarFormularioPedido();
                cargarTablaPedidos(null, null);
                refrescarCombosEntregas();
            } else {
                mostrarError("No se pudo registrar el pedido.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al registrar pedido", e);
        }
    }

    private void actualizarPedido() {
        String idStr = txtIdPedido.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione un pedido de la tabla para actualizar.", tablaPedidos);
            return;
        }

        String direccion = txtDireccionPedido.getText().trim();
        if (direccion.isEmpty()) {
            mostrarAlertaValidacion("La dirección del pedido es obligatoria.", txtDireccionPedido);
            return;
        }
        if (direccion.length() > 100) {
            mostrarAlertaValidacion("La dirección del pedido no puede superar los 100 caracteres.", txtDireccionPedido);
            return;
        }

        TipoPedido tipo = (TipoPedido) cbTipoPedido.getSelectedItem();
        EstadoPedido estado = (EstadoPedido) cbEstadoPedido.getSelectedItem();

        try {
            int id = Integer.parseInt(idStr);
            Pedido pedido = new Pedido(id, direccion, tipo, estado);
            boolean exito = pedidoDAO.update(pedido);
            if (exito) {
                mostrarExito("Pedido #" + id + " actualizado correctamente.");
                limpiarFormularioPedido();
                cargarTablaPedidos(null, null);
                refrescarCombosEntregas();
                cargarTablaEntregas();
            } else {
                mostrarError("No se encontró el pedido para actualizar.");
            }
        } catch (NumberFormatException e) {
            mostrarError("ID de pedido inválido.");
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al actualizar pedido", e);
        }
    }

    private void eliminarPedido() {
        String idStr = txtIdPedido.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione un pedido de la tabla para eliminar.", tablaPedidos);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el pedido ID #" + idStr + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(idStr);
            boolean exito = pedidoDAO.delete(id);
            if (exito) {
                mostrarExito("Pedido #" + id + " eliminado correctamente.");
                limpiarFormularioPedido();
                cargarTablaPedidos(null, null);
                refrescarCombosEntregas();
                cargarTablaEntregas();
            } else {
                mostrarError("No se pudo eliminar el pedido.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            mostrarError("No se puede eliminar el pedido porque tiene entregas asociadas.\nElimine primero las entregas asociadas.");
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al eliminar pedido", e);
        }
    }

    private void filtrarPedidos() {
        String tipoFiltro = cbFiltroTipoPedido.getSelectedItem().toString();
        String estadoFiltro = cbFiltroEstadoPedido.getSelectedItem().toString();
        cargarTablaPedidos(tipoFiltro, estadoFiltro);
    }

    private void limpiarFormularioPedido() {
        txtIdPedido.setText("Auto (Nuevo)");
        txtDireccionPedido.setText("");
        cbTipoPedido.setSelectedIndex(0);
        cbEstadoPedido.setSelectedIndex(0);
        tablaPedidos.clearSelection();
    }

    private void cargarTablaPedidos(String tipoFiltro, String estadoFiltro) {
        modeloPedidos.setRowCount(0);
        try {
            List<Pedido> lista = pedidoDAO.readByFiltros(tipoFiltro, estadoFiltro);
            for (Pedido p : lista) {
                modeloPedidos.addRow(new Object[]{
                        p.getId(),
                        p.getDireccion(),
                        p.getTipo().getEtiqueta(),
                        p.getEstado().getEtiqueta(),
                        p.calcularTiempoEntrega()
                });
            }
            lblMensajeEstado.setText("Pedidos cargados: " + lista.size());
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al cargar lista de pedidos", e);
        }
    }

    // =========================================================================
    // LÓGICA CRUD - ENTREGAS
    // =========================================================================
    @SuppressWarnings("unchecked")
    private void registrarEntrega() {
        ItemCombo<Pedido> itemPedido = (ItemCombo<Pedido>) cbPedidoEntrega.getSelectedItem();
        ItemCombo<Repartidor> itemRepartidor = (ItemCombo<Repartidor>) cbRepartidorEntrega.getSelectedItem();

        if (itemPedido == null) {
            mostrarAlertaValidacion("Debe seleccionar un pedido disponible.", cbPedidoEntrega);
            return;
        }
        if (itemRepartidor == null) {
            mostrarAlertaValidacion("Debe seleccionar un repartidor disponible.", cbRepartidorEntrega);
            return;
        }

        String fechaStr = txtFechaEntrega.getText().trim();
        String horaStr = txtHoraEntrega.getText().trim();

        LocalDate fecha;
        LocalTime hora;
        try {
            fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            mostrarAlertaValidacion("Formato de fecha inválido. Utilice formato YYYY-MM-DD (ej. 2026-10-05).", txtFechaEntrega);
            return;
        }

        try {
            if (horaStr.length() == 5) horaStr += ":00";
            hora = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            mostrarAlertaValidacion("Formato de hora inválido. Utilice formato HH:MM:SS (ej. 14:30:00).", txtHoraEntrega);
            return;
        }

        try {
            Entrega entrega = new Entrega(itemPedido.getId(), itemRepartidor.getId(), fecha, hora);
            boolean exito = entregaDAO.create(entrega);
            if (exito) {
                mostrarExito("Entrega registrada exitosamente con ID #" + entrega.getId());
                limpiarFormularioEntrega();
                cargarTablaEntregas();
            } else {
                mostrarError("No se pudo registrar la entrega.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al registrar entrega", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void actualizarEntrega() {
        String idStr = txtIdEntrega.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione una entrega de la tabla para actualizar.", tablaEntregas);
            return;
        }

        ItemCombo<Pedido> itemPedido = (ItemCombo<Pedido>) cbPedidoEntrega.getSelectedItem();
        ItemCombo<Repartidor> itemRepartidor = (ItemCombo<Repartidor>) cbRepartidorEntrega.getSelectedItem();

        if (itemPedido == null || itemRepartidor == null) {
            mostrarAlertaValidacion("Debe seleccionar un pedido y un repartidor válidos.", cbPedidoEntrega);
            return;
        }

        String fechaStr = txtFechaEntrega.getText().trim();
        String horaStr = txtHoraEntrega.getText().trim();

        LocalDate fecha;
        LocalTime hora;
        try {
            fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            mostrarAlertaValidacion("Formato de fecha inválido. Utilice formato YYYY-MM-DD.", txtFechaEntrega);
            return;
        }

        try {
            if (horaStr.length() == 5) horaStr += ":00";
            hora = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            mostrarAlertaValidacion("Formato de hora inválido. Utilice formato HH:MM:SS.", txtHoraEntrega);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Entrega entrega = new Entrega(id, itemPedido.getId(), itemRepartidor.getId(), fecha, hora);
            boolean exito = entregaDAO.update(entrega);
            if (exito) {
                mostrarExito("Entrega #" + id + " actualizada correctamente.");
                limpiarFormularioEntrega();
                cargarTablaEntregas();
            } else {
                mostrarError("No se encontró la entrega para actualizar.");
            }
        } catch (NumberFormatException e) {
            mostrarError("ID de entrega inválido.");
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al actualizar entrega", e);
        }
    }

    private void eliminarEntrega() {
        String idStr = txtIdEntrega.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione una entrega de la tabla para eliminar.", tablaEntregas);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar la entrega ID #" + idStr + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(idStr);
            boolean exito = entregaDAO.delete(id);
            if (exito) {
                mostrarExito("Entrega #" + id + " eliminada correctamente.");
                limpiarFormularioEntrega();
                cargarTablaEntregas();
            } else {
                mostrarError("No se pudo eliminar la entrega.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al eliminar entrega", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void filtrarEntregas() {
        ItemCombo<Repartidor> itemRep = (ItemCombo<Repartidor>) cbFiltroRepartidorEntrega.getSelectedItem();
        ItemCombo<Pedido> itemPed = (ItemCombo<Pedido>) cbFiltroPedidoEntrega.getSelectedItem();

        int idRepartidor = (itemRep != null) ? itemRep.getId() : 0;
        int idPedido = (itemPed != null) ? itemPed.getId() : 0;

        modeloEntregas.setRowCount(0);
        try {
            List<Entrega> lista;
            if (idRepartidor > 0) {
                lista = entregaDAO.readByRepartidor(idRepartidor);
            } else if (idPedido > 0) {
                lista = entregaDAO.readByPedido(idPedido);
            } else {
                lista = entregaDAO.readAll();
            }

            for (Entrega e : lista) {
                modeloEntregas.addRow(new Object[]{
                        e.getId(),
                        e.getIdPedido(),
                        e.getDireccionPedido(),
                        e.getIdRepartidor(),
                        e.getNombreRepartidor(),
                        e.getFechaFormateada(),
                        e.getHoraFormateada()
                });
            }
            lblMensajeEstado.setText("Entregas filtradas: " + lista.size());
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al filtrar entregas", e);
        }
    }

    private void limpiarFormularioEntrega() {
        txtIdEntrega.setText("Auto (Nuevo)");
        if (cbPedidoEntrega.getItemCount() > 0) cbPedidoEntrega.setSelectedIndex(0);
        if (cbRepartidorEntrega.getItemCount() > 0) cbRepartidorEntrega.setSelectedIndex(0);
        txtFechaEntrega.setText(LocalDate.now().toString());
        txtHoraEntrega.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        tablaEntregas.clearSelection();
    }

    private void cargarTablaEntregas() {
        modeloEntregas.setRowCount(0);
        try {
            List<Entrega> lista = entregaDAO.readAll();
            for (Entrega e : lista) {
                modeloEntregas.addRow(new Object[]{
                        e.getId(),
                        e.getIdPedido(),
                        e.getDireccionPedido(),
                        e.getIdRepartidor(),
                        e.getNombreRepartidor(),
                        e.getFechaFormateada(),
                        e.getHoraFormateada()
                });
            }
            lblMensajeEstado.setText("Entregas cargadas: " + lista.size());
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al cargar lista de entregas", e);
        }
    }

    /**
     * Carga y refresca los JComboBox de Pedido y Repartidor para la gestión de Entregas.
     * Muestra texto amigable (ID + Descripción) y almacena internamente la entidad y su ID.
     */
    private void refrescarCombosEntregas() {
        try {
            // Combos del formulario de entrega
            cbPedidoEntrega.removeAllItems();
            List<Pedido> pedidos = pedidoDAO.readAll();
            for (Pedido p : pedidos) {
                String desc = "ID: " + p.getId() + " - " + p.getDireccion() + " (" + p.getTipo().getEtiqueta() + ")";
                cbPedidoEntrega.addItem(new ItemCombo<>(p.getId(), desc, p));
            }

            cbRepartidorEntrega.removeAllItems();
            List<Repartidor> repartidores = repartidorDAO.readAll();
            for (Repartidor r : repartidores) {
                String desc = "ID: " + r.getId() + " - " + r.getNombre();
                cbRepartidorEntrega.addItem(new ItemCombo<>(r.getId(), desc, r));
            }

            // Combos del filtro de entrega
            cbFiltroRepartidorEntrega.removeAllItems();
            cbFiltroRepartidorEntrega.addItem(new ItemCombo<>(0, "[TODOS LOS REPARTIDORES]", null));
            for (Repartidor r : repartidores) {
                cbFiltroRepartidorEntrega.addItem(new ItemCombo<>(r.getId(), "ID: " + r.getId() + " - " + r.getNombre(), r));
            }

            cbFiltroPedidoEntrega.removeAllItems();
            cbFiltroPedidoEntrega.addItem(new ItemCombo<>(0, "[TODOS LOS PEDIDOS]", null));
            for (Pedido p : pedidos) {
                cbFiltroPedidoEntrega.addItem(new ItemCombo<>(p.getId(), "ID: " + p.getId() + " - " + p.getDireccion(), p));
            }

        } catch (SQLException e) {
            manejarExcepcionSQL("Error al actualizar combos desplegables", e);
        }
    }

    private <T> void seleccionarComboPorId(JComboBox<ItemCombo<T>> combo, int idBuscado) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            ItemCombo<T> item = combo.getItemAt(i);
            if (item != null && item.getId() == idBuscado) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // =========================================================================
    // LÓGICA CRUD - CLIENTES (ClienteDAO)
    // =========================================================================
    private void registrarCliente() {
        String nombre = txtNombreCliente.getText().trim();
        String email = txtEmailCliente.getText().trim();
        String telefono = txtTelefonoCliente.getText().trim();
        String direccion = txtDireccionCliente.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlertaValidacion("El nombre del cliente es obligatorio.", txtNombreCliente);
            return;
        }
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            mostrarAlertaValidacion("Ingrese un correo electrónico válido (ej. usuario@dominio.cl).", txtEmailCliente);
            return;
        }

        try {
            Cliente c = new Cliente(nombre, email, telefono, direccion);
            boolean exito = clienteDAO.create(c);
            if (exito) {
                mostrarExito("Cliente registrado exitosamente con ID #" + c.getId());
                limpiarFormularioCliente();
                cargarTablaClientes();
            } else {
                mostrarError("No se pudo registrar el cliente.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al registrar cliente", e);
        }
    }

    private void actualizarCliente() {
        String idStr = txtIdCliente.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione un cliente de la tabla para actualizar.", tablaClientes);
            return;
        }

        String nombre = txtNombreCliente.getText().trim();
        String email = txtEmailCliente.getText().trim();
        String telefono = txtTelefonoCliente.getText().trim();
        String direccion = txtDireccionCliente.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlertaValidacion("El nombre del cliente es obligatorio.", txtNombreCliente);
            return;
        }
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            mostrarAlertaValidacion("Ingrese un correo electrónico válido.", txtEmailCliente);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Cliente c = new Cliente(id, nombre, email, telefono, direccion);
            boolean exito = clienteDAO.update(c);
            if (exito) {
                mostrarExito("Cliente #" + id + " actualizado correctamente.");
                limpiarFormularioCliente();
                cargarTablaClientes();
            } else {
                mostrarError("No se encontró el cliente para actualizar.");
            }
        } catch (NumberFormatException e) {
            mostrarError("ID de cliente inválido.");
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al actualizar cliente", e);
        }
    }

    private void eliminarCliente() {
        String idStr = txtIdCliente.getText().trim();
        if (idStr.isEmpty() || idStr.startsWith("Auto")) {
            mostrarAlertaValidacion("Seleccione un cliente de la tabla para eliminar.", tablaClientes);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar al cliente ID #" + idStr + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(idStr);
            boolean exito = clienteDAO.delete(id);
            if (exito) {
                mostrarExito("Cliente #" + id + " eliminado correctamente.");
                limpiarFormularioCliente();
                cargarTablaClientes();
            } else {
                mostrarError("No se pudo eliminar el cliente.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al eliminar cliente", e);
        }
    }

    private void limpiarFormularioCliente() {
        txtIdCliente.setText("Auto (Nuevo)");
        txtNombreCliente.setText("");
        txtEmailCliente.setText("");
        txtTelefonoCliente.setText("");
        txtDireccionCliente.setText("");
        tablaClientes.clearSelection();
    }

    private void cargarTablaClientes() {
        modeloClientes.setRowCount(0);
        try {
            List<Cliente> lista = clienteDAO.readAll();
            for (Cliente c : lista) {
                modeloClientes.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getEmail(),
                        c.getTelefono(),
                        c.getDireccion()
                });
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("Error al cargar lista de clientes", e);
        }
    }

    /**
     * Prueba conectividad y recarga todas las tablas y listas desplegables.
     */
    public void verificarYRecargarTodo() {
        boolean ok = ConexionDB.probarConexion();
        if (ok) {
            cargarTablaRepartidores();
            cargarTablaPedidos(null, null);
            cargarTablaEntregas();
            cargarTablaClientes();
            refrescarCombosEntregas();
            lblMensajeEstado.setText("● Conectado a MySQL (" + ConexionDB.getBaseDatos() + ") - Datos sincronizados.");
            lblMensajeEstado.setForeground(new Color(25, 135, 84));
        } else {
            lblMensajeEstado.setText("● Sin conexión a MySQL. Verifique que el servicio esté iniciado e importe speedfast_db.sql.");
            lblMensajeEstado.setForeground(new Color(220, 53, 69));
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE DIÁLOGO Y FEEDBACK AL USUARIO
    // =========================================================================
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAlertaValidacion(String mensaje, Component componenteFoco) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación de Entrada", JOptionPane.WARNING_MESSAGE);
        if (componenteFoco != null) {
            componenteFoco.requestFocusInWindow();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error del Sistema", JOptionPane.ERROR_MESSAGE);
    }

    private void manejarExcepcionSQL(String titulo, SQLException e) {
        System.err.println(titulo + ": " + e.getMessage());
        String mensaje = titulo + ":\n" + e.getMessage();
        if (e.getMessage() != null && e.getMessage().contains("Communications link failure")) {
            mensaje += "\n\nSugerencia: Verifique que el servicio de MySQL esté iniciado en el puerto configurado.";
        }
        JOptionPane.showMessageDialog(this, mensaje, "Excepción SQL", JOptionPane.ERROR_MESSAGE);
    }
}
