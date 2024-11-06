import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // Asegúrate de importar ResultSet
import java.sql.SQLException;
import java.sql.Statement; // Importa Statement aquí


public class RegistroUsuario extends JFrame {
    private JPanel panelRegistro;
    private JTextField textNombreUsuario;
    private JPasswordField textContraseña;
    private JButton registrarButton;
    private JLabel REGISTRARSE;
    private JLabel NOMBRE;
    private JLabel CONTRASEÑA;
    private JTextField textNit;
    private JTextField textTelefono;
    private JTextField textCorreo;
    private JLabel NIT;
    private JLabel TELEFONO;
    private JLabel CORREO;

    public RegistroUsuario() {
        initComponents();

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });

        setContentPane(panelRegistro);
        setTitle("Registro de Usuario");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {

    }

    private void registrarUsuario() {
        String nombreUsuario = textNombreUsuario.getText();
        String contrasena = new String(textContraseña.getPassword());
        String NIT = textNit.getText();
        String TELEFONO = textTelefono.getText();
        String CORREO = textCorreo.getText();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty() || NIT.isEmpty() || TELEFONO.isEmpty() || CORREO.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtUsuario = null;
        PreparedStatement pstmtCliente = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");


            String sqlUsuario = "INSERT INTO REGISTRO (USUARIO, PASS, NIT, TELEFONO, CORREO, ROL) VALUES (?, ?, ?, ?, ?, 'CLIENTE')";
            pstmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            pstmtUsuario.setString(1, nombreUsuario);
            pstmtUsuario.setString(2, contrasena); // Considera hashear esta contraseña
            pstmtUsuario.setString(3, NIT);
            pstmtUsuario.setString(4, TELEFONO);
            pstmtUsuario.setString(5, CORREO);

            int filasAfectadas = pstmtUsuario.executeUpdate();

            if (filasAfectadas > 0) {

                ResultSet generatedKeys = pstmtUsuario.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idUsuario = generatedKeys.getInt(1);


                    String sqlCliente = "INSERT INTO CLIENTES (ID_REGISTRO, USUARIO, TELEFONO, CORREO) VALUES (?, ?, ?, ?)";
                    pstmtCliente = conn.prepareStatement(sqlCliente);
                    pstmtCliente.setInt(1, idUsuario);
                    pstmtCliente.setString(2, nombreUsuario);
                    pstmtCliente.setString(3, TELEFONO);
                    pstmtCliente.setString(4, CORREO);
                    pstmtCliente.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.");


                    textNombreUsuario.setText("");
                    textContraseña.setText("");
                    textNit.setText("");
                    textTelefono.setText("");
                    textCorreo.setText("");

                    LOGIN login = new LOGIN();
                    login.setVisible(true);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el usuario.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario: " + e.getMessage());
        } finally {
            try {
                if (pstmtUsuario != null) pstmtUsuario.close();
                if (pstmtCliente != null) pstmtCliente.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegistroUsuario ventana = new RegistroUsuario();
            ventana.setVisible(true);
        });
    }
}
