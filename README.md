# SpeedFast - Sistema de Gestión de Pedidos y Entregas
**Asignatura:** Desarrollo Orientado a Objetos II  
**Evaluación:** Sumativa Semana 8  
**Institución:** Duoc UC  

---

## 📌 Descripción del Proyecto

Este proyecto corresponde a la entrega final de la **Semana 8** para la empresa de logística ficticia **SpeedFast**.  
Es una aplicación de escritorio desarrollada en **Java Swing** que se conecta a una base de datos relacional **MySQL** mediante **JDBC**, implementando el patrón de diseño **DAO (Data Access Object)** para realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) en tiempo real.

El sistema permite gestionar:
- **Repartidores:** Registro, edición, eliminación y listado.
- **Pedidos:** Registro con tipo (`COMIDA`, `ENCOMIENDA`, `EXPRESS`) y estado (`PENDIENTE`, `EN_REPARTO`, `ENTREGADO`), con filtros de búsqueda.
- **Entregas:** Registro de entregas asociando pedidos y repartidores con fecha y hora mediante listas desplegables (`JComboBox`), mostrando tablas con `JOIN` relacional.
- **Clientes:** Módulo CRUD solicitado en la pauta oficial (`ClienteDAO`).

---

## ⚙️ Requisitos para Ejecutar

- **Java Development Kit (JDK):** Versión 21 (o superior, compatible con JDK 17+).
- **Servidor de Base de Datos:** MySQL o MariaDB (XAMPP, MySQL Server o MariaDB).
- **IDE Recomendado:** IntelliJ IDEA (o Eclipse / NetBeans).

---

## 🚀 Cómo Iniciar el Proyecto (Paso a Paso)

### 1. Cargar la Base de Datos
1. Inicia el servicio de MySQL (por ejemplo desde el panel de **XAMPP** o el servicio de Windows).
2. Abre tu gestor de base de datos preferido (phpMyAdmin, MySQL Workbench o terminal).
3. Abre y ejecuta el archivo:
   ```
   database/speedfast_db.sql
   ```
   *Este script crea la base de datos `speedfast_db`, las tablas (`repartidores`, `pedidos`, `entregas`, `clientes`), los datos de prueba y el usuario de la aplicación.*

### 2. Abrir el Proyecto en IntelliJ IDEA
1. Abre **IntelliJ IDEA**.
2. Haz clic en **Open** (Abrir) y selecciona la carpeta **`semana_8`**.
3. El proyecto ya incluye el archivo `semana_8.iml` y el driver en `lib/mysql-connector-j-8.3.0.jar`, por lo que IntelliJ reconocerá automáticamente la librería.

### 3. Ejecutar la Aplicación
1. Navega en la carpeta del proyecto a:  
   `src/cl/speedfast/main/Main.java`
2. Haz clic derecho sobre el archivo y selecciona **Run 'Main.main()'** (o presiona `Shift + F10`).
3. Se abrirá la ventana principal de SpeedFast conectada a la base de datos.

---

## 📁 Estructura del Código

El proyecto está organizado en paquetes siguiendo una arquitectura limpia por capas:

```
semana_8/
├── database/
│   └── speedfast_db.sql        # Script SQL de la base de datos y datos de prueba
├── lib/
│   └── mysql-connector-j-8.3.0.jar # Conector JDBC oficial de MySQL
├── src/
│   └── cl/speedfast/
│       ├── conexion/
│       │   └── ConexionDB.java     # Manejo de la conexión JDBC y cierre seguro de recursos
│       ├── modelo/
│       │   ├── Repartidor.java     # Clase entidad Repartidor
│       │   ├── Pedido.java         # Clase entidad Pedido (con cálculo de tiempo estimado)
│       │   ├── Entrega.java        # Clase entidad Entrega
│       │   ├── Cliente.java        # Clase entidad Cliente
│       │   ├── TipoPedido.java     # Enum con los tipos de pedido
│       │   └── EstadoPedido.java   # Enum con los estados de pedido
│       ├── dao/
│       │   ├── RepartidorDAO.java  # Operaciones CRUD para tabla repartidores
│       │   ├── PedidoDAO.java      # Operaciones CRUD y filtros para tabla pedidos
│       │   ├── EntregaDAO.java     # Operaciones CRUD con JOINs para tabla entregas
│       │   └── ClienteDAO.java     # Operaciones CRUD para tabla clientes
│       ├── vista/
│       │   ├── VentanaPrincipal.java # Interfaz gráfica Swing con formularios y tablas
│       │   └── ItemCombo.java      # Clase auxiliar para JComboBox (guarda el ID internamente)
│       └── main/
│           └── Main.java           # Clase con el método main() que inicia el programa
├── semana_8.iml                    # Archivo de configuración de IntelliJ IDEA
├── .gitignore                      # Filtro para Git (ignora bin, .idea temporales, etc.)
└── README.md                       # Documentación del proyecto
```

---

## 💡 Aspectos Clave de la Implementación

1. **Persistencia Segura (JDBC y PreparedStatement):**  
   Todas las consultas SQL en los DAO utilizan `PreparedStatement` con parámetros `?` para evitar cualquier inyección SQL, y los recursos (`ResultSet`, `PreparedStatement`, `Connection`) se cierran siempre en bloques `finally`.

2. **Listas Desplegables con IDs (`ItemCombo<T>`):**  
   Al crear o editar una Entrega, los `JComboBox` muestran textos legibles (por ejemplo: *"ID: 1 - Carlos Mendoza"*), pero conservan internamente el ID numérico para guardarlo en la base de datos. Si se agrega o borra un repartidor o pedido, los combos se actualizan automáticamente.

3. **Consultas con JOIN:**  
   En la tabla de entregas se utiliza una consulta `INNER JOIN` entre las tablas `entregas`, `pedidos` y `repartidores` para mostrar en pantalla el nombre del repartidor y la dirección del pedido en lugar de solo números.

4. **Validaciones y Control de Errores:**  
   - Los formularios validan campos obligatorios, formatos de fecha (`YYYY-MM-DD`) y formatos de hora (`HH:MM:SS`), colocando el foco en el campo que tenga error.
   - Si se intenta eliminar un repartidor o pedido que ya tiene entregas registradas, el sistema atrapa la excepción de integridad referencial (`SQLIntegrityConstraintViolationException`) y muestra un mensaje comprensible en pantalla.

5. **Credenciales de Base de Datos:**  
   La conexión está configurada por defecto para conectarse con el usuario de la aplicación (`speedfast` / `speedfast123`) e incluye fallback automático a `root` (sin contraseña) por si el docente lo prueba con la configuración por defecto de XAMPP.
