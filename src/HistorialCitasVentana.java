import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HistorialCitasVentana extends JFrame {
    private JTable historialTable;
    private JButton editarDetallesButton;
    private JButton generarFacturaButton;
    private JLabel detallesCitaLabel;
    private String rolUsuario;
    private Connection conexion;
    private int idCita;
    private JButton actualizarButton;


    public HistorialCitasVentana(String rol, int idCita) {
        this.idCita = idCita;
        rolUsuario = rol;
        setTitle("Historial de Citas - " + rol);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        detallesCitaLabel = new JLabel("Detalles de la cita seleccionada:");
        historialTable = new JTable();
        editarDetallesButton = new JButton("Editar Detalles");
        editarDetallesButton.setVisible("VETERINARIO".equalsIgnoreCase(rol) || "ADMINISTRADOR".equalsIgnoreCase(rol));


        generarFacturaButton = new JButton("Generar Factura");

        generarFacturaButton.setVisible("RECEPCIONISTA".equalsIgnoreCase(rol) || "ADMINISTRADOR".equalsIgnoreCase(rol));


        actualizarButton = new JButton("Actualizar");
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatosHistorial();
            }
        });


        add(new JScrollPane(historialTable), BorderLayout.CENTER);
        add(detallesCitaLabel, BorderLayout.NORTH);
        add(editarDetallesButton, BorderLayout.SOUTH);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editarDetallesButton);
        buttonPanel.add(generarFacturaButton);
        add(buttonPanel, BorderLayout.SOUTH);


        conectarBD();
        cargarDatosHistorialPorIdCita();


        editarDetallesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarDetallesCita();
            }
        });


        generarFacturaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarFactura();
            }
        });
    }

    private void cargarDatosHistorialPorIdCita() {
        try {

            String query = "SELECT ID_HISTORIAL, ID_CLIENTE, ID_MASCOTA, FECHA_CITA, HORA_CITA, MOTIVO, ESTADO, DETALLES FROM HISTORIAL_CITAS";
            PreparedStatement statement = conexion.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Historial");
            model.addColumn("ID Cliente");
            model.addColumn("ID Mascota");
            model.addColumn("Fecha Cita");
            model.addColumn("Hora Cita");
            model.addColumn("Motivo");
            model.addColumn("Estado");
            model.addColumn("Detalles");

            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getInt("ID_HISTORIAL"),
                        resultSet.getInt("ID_CLIENTE"),
                        resultSet.getInt("ID_MASCOTA"),
                        resultSet.getDate("FECHA_CITA"),
                        resultSet.getTime("HORA_CITA"),
                        resultSet.getString("MOTIVO"),
                        resultSet.getString("ESTADO"),
                        resultSet.getString("DETALLES")
                });
            }
            historialTable.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del historial", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void generarFactura() {

        if (!"RECEPCIONISTA".equalsIgnoreCase(rolUsuario) && !"ADMINISTRADOR".equalsIgnoreCase(rolUsuario)) {
            JOptionPane.showMessageDialog(this, "No tiene permisos para generar facturas.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaSeleccionada = historialTable.getSelectedRow();
        if (filaSeleccionada != -1) {
            int idHistorial = (int) historialTable.getValueAt(filaSeleccionada, 0);

            String motivo = (String) historialTable.getValueAt(filaSeleccionada, 5);
            double total = calcularTotalPorMotivo(motivo); // Método para calcular el total según el motivo de la cita


            try {
                String insertFacturaQuery = "INSERT INTO FACTURAS (ID_HISTORIAL, FECHA_FACTURA, TOTAL, METODO_PAGO) VALUES (?, ?, ?, ?)";
                PreparedStatement statementFactura = conexion.prepareStatement(insertFacturaQuery, Statement.RETURN_GENERATED_KEYS);

                statementFactura.setInt(1, idHistorial);
                statementFactura.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                statementFactura.setDouble(3, total);


                String metodoPago = "EFECTIVO";
                statementFactura.setString(4, metodoPago);

                int filasInsertadas = statementFactura.executeUpdate();

                if (filasInsertadas > 0) {
                    ResultSet generatedKeys = statementFactura.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int idFactura = generatedKeys.getInt(1);

                        String insertHistorialFacturaQuery = "INSERT INTO HISTORIAL_FACTURAS (ID_FACTURA, ID_HISTORIAL, FECHA_FACTURA, TOTAL, METODO_PAGO) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement statementHistorialFactura = conexion.prepareStatement(insertHistorialFacturaQuery);

                        statementHistorialFactura.setInt(1, idFactura);
                        statementHistorialFactura.setInt(2, idHistorial);
                        statementHistorialFactura.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                        statementHistorialFactura.setDouble(4, total);
                        statementHistorialFactura.setString(5, metodoPago);

                        int filasInsertadasHistorial = statementHistorialFactura.executeUpdate();
                        if (filasInsertadasHistorial > 0) {
                            JOptionPane.showMessageDialog(this, "Factura generada y registrada en el historial correctamente.");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al generar la factura", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para generar la factura", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }


    private double calcularTotalPorMotivo(String motivo) {

        switch (motivo) {
            case "VETERINARIO GENERAL":
                return 100.0;
            case "ESTETICA":
                return 80.0;
            case "ODONTOLOGIA":
                return 120.0;
            default:
                return 0.0;
        }
    }

    public void agregarCita(int idCita, int idCliente, int idMascota, String fechaCita, String horaCita, String motivo) {
        DefaultTableModel model = (DefaultTableModel) historialTable.getModel();
        model.addRow(new Object[]{
                null,
                idCliente,
                idMascota,
                fechaCita,
                horaCita,
                motivo,
                "COMPLETADA",
                ""
        });
    }

    private void conectarBD() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/veterinaria";
            String user = "root";
            String password = "12345";
            conexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosHistorial() {
        try {
            String query = "SELECT ID_HISTORIAL, ID_CLIENTE, ID_MASCOTA, FECHA_CITA, HORA_CITA, MOTIVO, ESTADO, DETALLES FROM HISTORIAL_CITAS";
            PreparedStatement statement = conexion.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Historial");
            model.addColumn("ID Cliente");
            model.addColumn("ID Mascota");
            model.addColumn("Fecha Cita");
            model.addColumn("Hora Cita");
            model.addColumn("Motivo");
            model.addColumn("Estado");
            model.addColumn("Detalles");

            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getInt("ID_HISTORIAL"),
                        resultSet.getInt("ID_CLIENTE"),
                        resultSet.getInt("ID_MASCOTA"),
                        resultSet.getDate("FECHA_CITA"),
                        resultSet.getTime("HORA_CITA"),
                        resultSet.getString("MOTIVO"),
                        resultSet.getString("ESTADO"),
                        resultSet.getString("DETALLES")
                });
            }
            historialTable.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del historial", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarDetallesCita() {
        int filaSeleccionada = historialTable.getSelectedRow();
        if (filaSeleccionada != -1) {
            int idHistorial = (int) historialTable.getValueAt(filaSeleccionada, 0);

            String nuevoEstado = JOptionPane.showInputDialog(this, "Ingrese el nuevo estado (COMPLETADA, CANCELADA, NO SE PRESENTÓ):");
            String nuevosDetalles = JOptionPane.showInputDialog(this, "Ingrese los nuevos detalles:");

            if (nuevoEstado != null && nuevosDetalles != null) {
                try {
                    String updateQuery = "UPDATE HISTORIAL_CITAS SET ESTADO = ?, DETALLES = ? WHERE ID_HISTORIAL = ?";
                    PreparedStatement statement = conexion.prepareStatement(updateQuery);
                    statement.setString(1, nuevoEstado);
                    statement.setString(2, nuevosDetalles);
                    statement.setInt(3, idHistorial);

                    int filasActualizadas = statement.executeUpdate();
                    if (filasActualizadas > 0) {
                        JOptionPane.showMessageDialog(this, "Detalles de la cita actualizados correctamente");
                        cargarDatosHistorial();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al actualizar los detalles de la cita", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para editar", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HistorialCitasVentana ventana = new HistorialCitasVentana("VETERINARIO", 1);
            ventana.setVisible(true);
        });
    }
}