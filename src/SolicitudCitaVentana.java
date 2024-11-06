import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Importación añadida
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SolicitudCitaVentana extends JFrame {
    private JTextField fechaCitaText;
    private JTextField horaCitaText;
    private JComboBox<String> motivoComboBox;
    private JTextField nombreMascotaText;
    private JTextField razaMascotaText;
    private JTextField edadMascotaText;
    private JButton agendarCitaButton;
    private Connection conexion;
    private HistorialCitasVentana historialCitasVentana;
    private JTable historialTable;

    public SolicitudCitaVentana(int idUsuario, HistorialCitasVentana historialCitasVentana) {
        this.historialCitasVentana = historialCitasVentana;

        setTitle("Agendar Cita");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());


        fechaCitaText = new JTextField(15);
        horaCitaText = new JTextField(15);
        motivoComboBox = new JComboBox<>(new String[]{"VETERINARIO GENERAL", "ESTETICA", "ODONTOLOGIA"});
        nombreMascotaText = new JTextField(15);
        razaMascotaText = new JTextField(15);
        edadMascotaText = new JTextField(15);
        agendarCitaButton = new JButton("Agendar Cita");


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Fecha Cita (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; add(fechaCitaText, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Hora Cita (HH:MM):"), gbc);
        gbc.gridx = 1; add(horaCitaText, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1; add(motivoComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Nombre Mascota:"), gbc);
        gbc.gridx = 1; add(nombreMascotaText, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Raza Mascota:"), gbc);
        gbc.gridx = 1; add(razaMascotaText, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(new JLabel("Edad Mascota:"), gbc);
        gbc.gridx = 1; add(edadMascotaText, gbc);

        gbc.gridx = 1; gbc.gridy = 6; add(agendarCitaButton, gbc);

        conectar();

        agendarCitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agendarCita(idUsuario);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JTable getHistorialTable() {
        return historialTable;
    }

    public void agregarCita(int idCita, int idCliente, int idMascota, String fechaCita, String horaCita, String motivo) {
        DefaultTableModel model = (DefaultTableModel) historialTable.getModel();
        model.addRow(new Object[]{
                idCita,
                idCliente,
                idMascota,
                fechaCita,
                horaCita,
                motivo,
                "COMPLETADA",
                ""
        });
    }


    private void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void agendarCita(int idUsuario) {
        try {

            String obtenerClienteSql = "SELECT ID_CLIENTE FROM CLIENTES WHERE ID_REGISTRO = ?";
            PreparedStatement psCliente = conexion.prepareStatement(obtenerClienteSql);
            psCliente.setInt(1, idUsuario);
            ResultSet rsCliente = psCliente.executeQuery();
            int idCliente = 0;
            if (rsCliente.next()) {
                idCliente = rsCliente.getInt("ID_CLIENTE");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el cliente asociado al usuario.");
                return;
            }


            String checkCitaSql = "SELECT COUNT(*) FROM SOLICITUD_CITA WHERE FECHA_CITA = ? AND HORA_CITA = ?";
            PreparedStatement psCheck = conexion.prepareStatement(checkCitaSql);
            psCheck.setDate(1, Date.valueOf(fechaCitaText.getText()));
            psCheck.setTime(2, Time.valueOf(horaCitaText.getText() + ":00"));
            ResultSet rs = psCheck.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                JOptionPane.showMessageDialog(this, "La fecha y hora seleccionadas ya están ocupadas. Por favor, elige otra.");
                return;
            }


            String sqlMascota = "INSERT INTO MASCOTAS (NOMBRE_MASCOTA, RAZA, EDAD) VALUES (?, ?, ?)";
            PreparedStatement psMascota = conexion.prepareStatement(sqlMascota, Statement.RETURN_GENERATED_KEYS);
            psMascota.setString(1, nombreMascotaText.getText());
            psMascota.setString(2, razaMascotaText.getText());
            psMascota.setInt(3, Integer.parseInt(edadMascotaText.getText()));
            psMascota.executeUpdate();

            ResultSet generatedKeys = psMascota.getGeneratedKeys();
            int idMascota = 0;
            if (generatedKeys.next()) {
                idMascota = generatedKeys.getInt(1);
            }


            String sqlCita = "INSERT INTO SOLICITUD_CITA (ID_CLIENTE, FECHA_CITA, HORA_CITA, MOTIVO, ID_MASCOTA) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psCita = conexion.prepareStatement(sqlCita, Statement.RETURN_GENERATED_KEYS);
            psCita.setInt(1, idCliente);
            psCita.setDate(2, Date.valueOf(fechaCitaText.getText()));
            psCita.setTime(3, Time.valueOf(horaCitaText.getText() + ":00"));
            psCita.setString(4, (String) motivoComboBox.getSelectedItem());
            psCita.setInt(5, idMascota);
            psCita.executeUpdate();

            ResultSet citaGeneratedKeys = psCita.getGeneratedKeys();
            int idCita = 0;
            if (citaGeneratedKeys.next()) {
                idCita = citaGeneratedKeys.getInt(1);
            }


            String sqlHistorialCita = "INSERT INTO HISTORIAL_CITAS (ID_CLIENTE, ID_CITA, ID_MASCOTA, FECHA_CITA, HORA_CITA, MOTIVO, ESTADO, DETALLES) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psHistorial = conexion.prepareStatement(sqlHistorialCita);
            psHistorial.setInt(1, idCliente);
            psHistorial.setInt(2, idCita);
            psHistorial.setInt(3, idMascota);
            psHistorial.setDate(4, Date.valueOf(fechaCitaText.getText()));
            psHistorial.setTime(5, Time.valueOf(horaCitaText.getText() + ":00"));
            psHistorial.setString(6, (String) motivoComboBox.getSelectedItem());
            psHistorial.setString(7, "COMPLETADA");
            psHistorial.setString(8, "");

            psHistorial.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cita agendada y mascota registrada con éxito.");


            historialCitasVentana.agregarCita(idCita, idCliente, idMascota, fechaCitaText.getText(), horaCitaText.getText(), (String) motivoComboBox.getSelectedItem());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error SQL: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Datos inválidos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error desconocido: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HistorialCitasVentana historialCitasVentana = new HistorialCitasVentana("RECEPCIONISTA",1); // Crear instancia de historial

            new SolicitudCitaVentana(1, historialCitasVentana);
        });
    }
}
