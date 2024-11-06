import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HistorialFacturaVentana extends JFrame {
    private JTable historialTable;
    private JButton buscarButton;
    private JButton restablecerButton;
    private JTextField buscarField;
    private JLabel detallesFacturaLabel;
    private String rolUsuario;
    private Connection conexion;
    private int idFactura;


    public HistorialFacturaVentana(String rol) {
        rolUsuario = rol;
        setTitle("Historial de Facturas - " + rol);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        detallesFacturaLabel = new JLabel("Detalles de la factura seleccionada:");
        historialTable = new JTable();


        buscarButton = new JButton("Buscar");
        restablecerButton = new JButton("Restablecer");
        buscarField = new JTextField(10);

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarFacturaPorId();
            }
        });

        restablecerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatosHistorial();
            }
        });


        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Buscar por ID de Factura:"));
        searchPanel.add(buscarField);
        searchPanel.add(buscarButton);
        searchPanel.add(restablecerButton);


        add(new JScrollPane(historialTable), BorderLayout.CENTER);
        add(detallesFacturaLabel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);


        conectarBD();
        cargarDatosHistorial();
    }

    private void insertarFacturaEnHistorial(int idFactura) {
        try {

            String queryFactura = "SELECT ID_FACTURA, ID_HISTORIAL, FECHA_FACTURA, TOTAL, METODO_PAGO, ESTADO FROM FACTURAS WHERE ID_FACTURA = ?";
            PreparedStatement statementFactura = conexion.prepareStatement(queryFactura);
            statementFactura.setInt(1, idFactura);
            ResultSet resultSet = statementFactura.executeQuery();

            if (resultSet.next()) {
                int idFacturaRecibido = resultSet.getInt("ID_FACTURA");
                int idHistorial = resultSet.getInt("ID_HISTORIAL");
                Date fechaFactura = resultSet.getDate("FECHA_FACTURA");
                double total = resultSet.getDouble("TOTAL");
                String metodoPago = resultSet.getString("METODO_PAGO");
                String metodoEstado = resultSet.getString("ESTADO");


                String queryHistorial = "INSERT INTO HISTORIAL_FACTURAS (ID_FACTURA, ID_HISTORIAL, FECHA_FACTURA, TOTAL, METODO_PAGO, ESTADO) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statementHistorial = conexion.prepareStatement(queryHistorial);
                statementHistorial.setInt(1, idFacturaRecibido);
                statementHistorial.setInt(2, idHistorial) ;
                statementHistorial.setDate(3, fechaFactura);
                statementHistorial.setDouble(4, total);
                statementHistorial.setString(5, metodoPago);
                statementHistorial.setString(6, metodoEstado);
                statementHistorial.executeUpdate();

                System.out.println("Factura agregada al historial exitosamente");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al agregar la factura al historial", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void cargarDatosHistorial() {
        try {
            String query = "SELECT ID_FACTURA, ID_HISTORIAL, FECHA_FACTURA, TOTAL, METODO_PAGO FROM FACTURAS";
            PreparedStatement statement = conexion.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Factura");
            model.addColumn("ID Historial");
            model.addColumn("Fecha Factura");
            model.addColumn("Total");
            model.addColumn("Método de Pago");

            while (resultSet.next()) {
                model.addRow(new Object[] {
                        resultSet.getInt("ID_FACTURA"),
                        resultSet.getInt("ID_HISTORIAL"),
                        resultSet.getDate("FECHA_FACTURA"),
                        resultSet.getDouble("TOTAL"),
                        resultSet.getString("METODO_PAGO")
                });
            }

            historialTable.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del historial", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarFacturaPorId() {
        try {
            String idFacturaStr = buscarField.getText().trim();
            if (idFacturaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un ID de factura para buscar.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idFactura = Integer.parseInt(idFacturaStr);
            String query = "SELECT ID_FACTURA, ID_HISTORIAL, FECHA_FACTURA, TOTAL, METODO_PAGO FROM FACTURAS WHERE ID_FACTURA = ?";
            PreparedStatement statement = conexion.prepareStatement(query);
            statement.setInt(1, idFactura);
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Factura");
            model.addColumn("ID Historial");
            model.addColumn("Fecha Factura");
            model.addColumn("Total");
            model.addColumn("Método de Pago");

            if (resultSet.next()) {
                model.addRow(new Object[] {
                        resultSet.getInt("ID_FACTURA"),
                        resultSet.getInt("ID_HISTORIAL"),
                        resultSet.getDate("FECHA_FACTURA"),
                        resultSet.getDouble("TOTAL"),
                        resultSet.getString("METODO_PAGO")
                });
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró una factura con ese ID.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }

            historialTable.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al realizar la búsqueda", "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HistorialFacturaVentana ventana = new HistorialFacturaVentana("RECEPCIONISTA");
            ventana.setVisible(true);
        });
    }
}
