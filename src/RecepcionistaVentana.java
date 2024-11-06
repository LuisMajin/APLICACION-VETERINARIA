import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RecepcionistaVentana extends JFrame {
    private JTable clientesTable;
    private JButton verHistorialCitasButton;
    private JButton verHistorialFacturasButton;
    private JButton cerrarSesionButton;
    private Connection conexion;

    public RecepcionistaVentana() {

        conectar();


        setTitle("Recepcionista - Gestión de Clientes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        clientesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        add(scrollPane, BorderLayout.CENTER);


        verHistorialCitasButton = new JButton("Ver Historial de Citas");
        verHistorialCitasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialCitas();
            }
        });


        verHistorialFacturasButton = new JButton("Ver Historial de Facturas");
        verHistorialFacturasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialFacturas();
            }
        });


        cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(verHistorialCitasButton);
        buttonPanel.add(verHistorialFacturasButton);
        buttonPanel.add(cerrarSesionButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Método para conectar a la base de datos
    private void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void abrirHistorialCitas() {

        HistorialCitasVentana historialCitasVentana = new HistorialCitasVentana("RECEPCIONISTA", 1);
        historialCitasVentana.setVisible(true);
    }

    private void abrirHistorialFacturas() {

        HistorialFacturaVentana historialFacturasVentana = new HistorialFacturaVentana("RECEPCIONISTA");
        historialFacturasVentana.setVisible(true);
    }

    private void cerrarSesion() {

        dispose();
        LOGIN loginVentana = new LOGIN();
        loginVentana.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            RecepcionistaVentana ventana = new RecepcionistaVentana();
            ventana.setVisible(true);
        });
    }
}
