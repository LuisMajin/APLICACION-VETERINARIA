import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LOGIN extends JFrame {
    private JPanel panellogin;
    private JTextField textusuario;
    private JPasswordField passwordField;
    private JButton INGRESARButton;
    private JButton CREARUSUARIOButton;
    Connection conexion;

    public LOGIN() {
        setTitle("Iniciar Sesión");
        setContentPane(panellogin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        INGRESARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarusuario();
            }
        });

        CREARUSUARIOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirVentanaRegistro();
            }
        });
    }

    private void abrirVentanaRegistro() {
        RegistroUsuario registroUsuario = new RegistroUsuario();
        registroUsuario.setVisible(true);
    }

    void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    void validarusuario() {
        conectar();
        String USUARIO = textusuario.getText();
        String PASS = String.valueOf(passwordField.getPassword());


        if (USUARIO.isEmpty() || PASS.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
            return;
        }

        try {
            String sql = "SELECT * FROM registro WHERE usuario = ? AND pass = ?";
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setString(1, USUARIO);
            pstmt.setString(2, PASS);
            System.out.println("Consultando usuario: " + USUARIO + " con contraseña: " + PASS);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("ROL");
                abrirVentanaPorRol(rol);
            } else {
                sql = "SELECT * FROM login WHERE usuario = ? AND pass = ?";
                pstmt = conexion.prepareStatement(sql);
                pstmt.setString(1, USUARIO);
                pstmt.setString(2, PASS);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String rol = rs.getString("ROL");
                    abrirVentanaPorRol(rol);
                } else {
                    JOptionPane.showMessageDialog(null, "Las credenciales no son correctas");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void abrirVentanaPorRol(String rol) {
        System.out.println("Rol obtenido: " + rol);
        switch (rol.toUpperCase()) {
            case "CLIENTE":
                new ClienteVentana(1).setVisible(true);
                break;
            case "ADMINISTRADOR":
                new AdministradorVentana().setVisible(true);
                break;
            case "VETERINARIO":
                new VeterinarioVentana().setVisible(true);
                break;
            case "RECEPCIONISTA":
                new RecepcionistaVentana().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "El rol no tiene permisos de acceso");
                break;
        }
        dispose();
    }

    public static void main(String[] args) {
        LOGIN login1 = new LOGIN();
        login1.setVisible(true);
    }
}
