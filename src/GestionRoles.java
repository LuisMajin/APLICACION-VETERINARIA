import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GestionRoles extends JFrame {
    private JTextField idText;
    private JComboBox<String> rolComboBox;
    private JTextField usuarioText;
    private JTextField nitText;
    private JTextField telefonoText;
    private JTextField correoText;
    private JPasswordField passText;
    private JButton consultarBoton;
    private JButton agregarBoton;
    private JButton actualizarBoton;
    private JButton eliminarBoton;
    private JTable tabla;
    private JPanel panelRoles;
    private JList<String> lista;
    private DefaultTableModel modTabla;
    private Connection conexion;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;
    private Integer idEliminado = null;

    public void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }


    public GestionRoles() {
        setTitle("Gestión de Roles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());




        panelRoles = new JPanel(new GridBagLayout());
        panelRoles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelRoles.setBackground(new Color(240, 240, 255));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel idLabel = new JLabel("ID Usuario:");
        idText = new JTextField(10);
        JLabel rolLabel = new JLabel("Rol:");
        rolComboBox = new JComboBox<>(new String[]{"ADMINISTRADOR", "VETERINARIO", "RECEPCIONISTA", "CLIENTE"});
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioText = new JTextField(10);
        JLabel nitLabel = new JLabel("NIT:");
        nitText = new JTextField(10);
        JLabel telefonoLabel = new JLabel("Teléfono:");
        telefonoText = new JTextField(10);
        JLabel correoLabel = new JLabel("Correo:");
        correoText = new JTextField(10);
        JLabel passLabel = new JLabel("Contraseña:");
        passText = new JPasswordField(10);


        consultarBoton = crearBoton("Consultar", new Color(0, 102, 204));
        agregarBoton = crearBoton("Agregar", new Color(0, 153, 51));
        actualizarBoton = crearBoton("Actualizar", new Color(255, 153, 0));
        eliminarBoton = crearBoton("Eliminar", new Color(204, 0, 0));


        int row = 0;
        panelRoles.add(idLabel, gbcPos(gbc, 0, row));
        panelRoles.add(idText, gbcPos(gbc, 1, row++));
        panelRoles.add(rolLabel, gbcPos(gbc, 0, row));
        panelRoles.add(rolComboBox, gbcPos(gbc, 1, row++));
        panelRoles.add(usuarioLabel, gbcPos(gbc, 0, row));
        panelRoles.add(usuarioText, gbcPos(gbc, 1, row++));
        panelRoles.add(nitLabel, gbcPos(gbc, 0, row));
        panelRoles.add(nitText, gbcPos(gbc, 1, row++));
        panelRoles.add(telefonoLabel, gbcPos(gbc, 0, row));
        panelRoles.add(telefonoText, gbcPos(gbc, 1, row++));
        panelRoles.add(correoLabel, gbcPos(gbc, 0, row));
        panelRoles.add(correoText, gbcPos(gbc, 1, row++));
        panelRoles.add(passLabel, gbcPos(gbc, 0, row));
        panelRoles.add(passText, gbcPos(gbc, 1, row++));


        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botonesPanel.setBackground(new Color(240, 240, 255));
        botonesPanel.add(consultarBoton);
        botonesPanel.add(agregarBoton);
        botonesPanel.add(actualizarBoton);
        botonesPanel.add(eliminarBoton);


        String[] columnas = {"ID Usuario", "Usuario", "Contraseña", "NIT", "Teléfono", "Correo", "Rol"};
        modTabla = new DefaultTableModel(null, columnas);
        tabla = new JTable(modTabla);
        tabla.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tabla);


        add(panelRoles, BorderLayout.NORTH);
        add(botonesPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);


        configurarListeners();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(120, 30));
        return boton;
    }

    private GridBagConstraints gbcPos(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }


    private void configurarListeners() {
        consultarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    consultar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
        agregarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    insertar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
        actualizarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actualizar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
        eliminarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    eliminar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
    }



    public void consultar() throws SQLException {
        conectar();
        String sql = "SELECT ID_USUARIO, USUARIO, PASS, NIT, TELEFONO, CORREO, ROL FROM LOGIN " +
                "UNION ALL " +
                "SELECT ID_REGISTRO AS ID_USUARIO, USUARIO, PASS, NIT, TELEFONO, CORREO, ROL FROM REGISTRO";

        st = conexion.createStatement();
        rs = st.executeQuery(sql);
        modTabla.setRowCount(0);

        while (rs.next()) {
            Object[] fila = new Object[7];
            fila[0] = rs.getInt("ID_USUARIO");
            fila[1] = rs.getString("USUARIO");
            fila[2] = rs.getString("PASS");
            fila[3] = rs.getString("NIT");
            fila[4] = rs.getString("TELEFONO");
            fila[5] = rs.getString("CORREO");
            fila[6] = rs.getString("ROL");
            modTabla.addRow(fila);
        }


        rs.close();
        st.close();
        conexion.close();
    }

    public void eliminarUsuario(int id) throws SQLException {
        conectar();


        idEliminado = id;


        String sqlDeleteRegistro = "DELETE FROM REGISTRO WHERE ID_REGISTRO = ?";
        ps = conexion.prepareStatement(sqlDeleteRegistro);
        ps.setInt(1, id);
        ps.executeUpdate();


        String sqlDeleteLogin = "DELETE FROM LOGIN WHERE ID_USUARIO = ?";
        ps = conexion.prepareStatement(sqlDeleteLogin);
        ps.setInt(1, id);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente.");
        ps.close();
        conexion.close();
    }

    public void insertar() throws SQLException {
        String rolSeleccionado = rolComboBox.getSelectedItem().toString();
        conectar();


        String sqlLogin = "INSERT INTO LOGIN (USUARIO, PASS, ROL) VALUES (?, ?, ?)";
        ps = conexion.prepareStatement(sqlLogin, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, usuarioText.getText());
        ps.setString(2, new String(passText.getPassword()));
        ps.setString(3, rolSeleccionado);

        int filasLogin = ps.executeUpdate();
        if (filasLogin > 0) {

            ResultSet rsLogin = ps.getGeneratedKeys();
            if (rsLogin.next()) {
                int idUsuarioGenerado = rsLogin.getInt(1);


                String sqlRegistro = "INSERT INTO REGISTRO (ID_REGISTRO, NIT, TELEFONO, CORREO, ROL) VALUES (?, ?, ?, ?, ?)";
                ps = conexion.prepareStatement(sqlRegistro);
                ps.setInt(1, idUsuarioGenerado);
                ps.setString(2, nitText.getText());
                ps.setString(3, telefonoText.getText());
                ps.setString(4, correoText.getText());
                ps.setString(5, rolSeleccionado);

                int filasRegistro = ps.executeUpdate();
                if (filasRegistro > 0) {
                    JOptionPane.showMessageDialog(this, "Registro exitoso.");
                    consultar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al insertar en REGISTRO.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al insertar en LOGIN.");
        }

        ps.close();
        conexion.close();
    }



    private int obtenerSiguienteId() throws SQLException {
        String sql = "SELECT IFNULL(MAX(ID_REGISTRO), 0) + 1 AS siguiente_id FROM REGISTRO " +
                "UNION ALL " +
                "SELECT IFNULL(MAX(ID_USUARIO), 0) + 1 FROM LOGIN";
        st = conexion.createStatement();
        rs = st.executeQuery(sql);
        int siguienteId = 1;

        if (rs.next()) {
            siguienteId = rs.getInt("siguiente_id");
        }


        rs.close();
        st.close();
        return siguienteId;
    }

    public void actualizar() throws SQLException {
        conectar();


        if (idText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID válido.");
            return;
        }


        String rolSeleccionado = rolComboBox.getSelectedItem().toString();
        String sqlCheck = (rolSeleccionado.equals("CLIENTE"))
                ? "SELECT COUNT(*) FROM REGISTRO WHERE ID_REGISTRO = ?"
                : "SELECT COUNT(*) FROM LOGIN WHERE ID_USUARIO = ?";

        ps = conexion.prepareStatement(sqlCheck);
        ps.setInt(1, Integer.parseInt(idText.getText()));
        ResultSet rs = ps.executeQuery();


        if (rs.next() && rs.getInt(1) == 0) {
            JOptionPane.showMessageDialog(this, "El ID proporcionado no existe.");
            return;
        }


        String sqlUpdate = (rolSeleccionado.equals("CLIENTE"))
                ? "UPDATE REGISTRO SET USUARIO = ?, PASS = ?, NIT = ?, TELEFONO = ?, CORREO = ? WHERE ID_REGISTRO = ?"
                : "UPDATE LOGIN SET USUARIO = ?, PASS = ?, NIT = ?, TELEFONO = ?, CORREO = ? WHERE ID_USUARIO = ?";

        ps = conexion.prepareStatement(sqlUpdate);
        ps.setString(1, usuarioText.getText());
        ps.setString(2, new String(passText.getPassword()));
        ps.setString(3, nitText.getText());
        ps.setString(4, telefonoText.getText());
        ps.setString(5, correoText.getText());
        ps.setInt(6, Integer.parseInt(idText.getText()));


        int filasAfectadas = ps.executeUpdate();
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(this, "Registro actualizado exitosamente.");
            consultar();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el registro. Asegúrese de que el ID exista.");
        }


        ps.close();
        rs.close();
        conexion.close();
    }

    public void eliminar() throws SQLException {
        conectar();


        if (idText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID válido.");
            return;
        }

        String rolSeleccionado = rolComboBox.getSelectedItem().toString();
        String sql;


        if (rolSeleccionado.equals("CLIENTE")) {
            sql = "DELETE FROM REGISTRO WHERE ID_REGISTRO = ?";
        } else {
            sql = "DELETE FROM LOGIN WHERE ID_USUARIO = ?";
        }

        ps = conexion.prepareStatement(sql);
        ps.setInt(1, Integer.parseInt(idText.getText()));

        int filasAfectadas = ps.executeUpdate();
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(this, "Registro eliminado exitosamente.");
            consultar();
        } else {
            JOptionPane.showMessageDialog(this, "Error al eliminar el registro. Asegúrese de que el ID exista.");
        }

        ps.close();
        conexion.close();
    }





    private void mostrarError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(GestionRoles::new);
    }

}
