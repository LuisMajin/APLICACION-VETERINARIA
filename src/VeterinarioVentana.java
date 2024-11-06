import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VeterinarioVentana extends JFrame {
    private JButton cerrarSesionButton;

    public VeterinarioVentana() {
        setTitle("Ventana de Veterinario");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JMenuBar menuBar = new JMenuBar();


        JMenu historialMenu = new JMenu("Menu");


        JMenuItem historialCitasItem = new JMenuItem("Historial de Citas");
        historialCitasItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialCitas();
            }
        });


        historialMenu.add(historialCitasItem);


        menuBar.add(historialMenu);


        setJMenuBar(menuBar);


        cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cerrarSesionButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void abrirHistorialCitas() {

        HistorialCitasVentana historialCitasVentana = new HistorialCitasVentana("VETERINARIO", 1);
        historialCitasVentana.setVisible(true);
    }

    private void cerrarSesion() {

        dispose();
        LOGIN loginVentana = new LOGIN();
        loginVentana.setVisible(true);
    }

    public static void main(String[] args) {
        VeterinarioVentana ventanaVeterinario = new VeterinarioVentana();
        ventanaVeterinario.setVisible(true);
    }
}
