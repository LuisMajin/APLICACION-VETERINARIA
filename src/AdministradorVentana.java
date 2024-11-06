import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdministradorVentana extends JFrame {
    public AdministradorVentana() {
        setTitle("Ventana de Administrador");
        setSize(600, 400);
        setLocationRelativeTo(null); // Centrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear la barra de menú
        JMenuBar menuBar = new JMenuBar();

        // Crear el menú "Gestión"
        JMenu gestionMenu = new JMenu("Menu");

        // Crear las opciones del menú
        JMenuItem gestionRolesItem = new JMenuItem("Gestión de Roles");
        gestionRolesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirGestionRoles();
            }
        });

        JMenuItem inventarioItem = new JMenuItem("Inventario");
        inventarioItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirInventario();
            }
        });

        JMenuItem historialCitasItem = new JMenuItem("Historial de Citas");
        historialCitasItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialCitas();
            }
        });

        JMenuItem historialFacturasItem = new JMenuItem("Facturas");
        historialFacturasItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialFacturas();
            }
        });

        // Crear la opción de cerrar sesión
        JMenuItem cerrarSesionItem = new JMenuItem("Cerrar Sesión");
        cerrarSesionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });

        // Agregar las opciones al menú
        gestionMenu.add(gestionRolesItem);
        gestionMenu.add(inventarioItem);
        gestionMenu.add(historialCitasItem);
        gestionMenu.add(historialFacturasItem);
        gestionMenu.addSeparator(); // Separador para organizar mejor el menú
        gestionMenu.add(cerrarSesionItem); // Agregar opción de cerrar sesión

        // Agregar el menú a la barra de menú
        menuBar.add(gestionMenu);

        // Configurar la barra de menú en la ventana
        setJMenuBar(menuBar);
    }

    private void abrirGestionRoles() {
        GestionRoles gestionRoles = new GestionRoles();
        gestionRoles.setVisible(true);
    }

    private void abrirInventario() {
        InventarioVentana inventarioVentana = new InventarioVentana();
        inventarioVentana.setVisible(true);
    }

    private void abrirHistorialCitas() {
        HistorialCitasVentana historialVentana = new HistorialCitasVentana("ADMINISTRADOR",1);
        historialVentana.setVisible(true);
    }

    private void abrirHistorialFacturas() {
        // Aquí deberías implementar la ventana del historial de facturas
        JOptionPane.showMessageDialog(this, "Ventana de Historial de Facturas no implementada.");
    }

    private void cerrarSesion() {
        // Cerrar la ventana actual y volver a la clase LOGIN
        dispose(); // Cierra la ventana actual
        LOGIN loginVentana = new LOGIN(); // Crear una instancia de la clase LOGIN
        loginVentana.setVisible(true); // Mostrar la ventana de LOGIN
    }

    public static void main(String[] args) {
        AdministradorVentana ventanaAdmin = new AdministradorVentana();
        ventanaAdmin.setVisible(true);
    }
}
