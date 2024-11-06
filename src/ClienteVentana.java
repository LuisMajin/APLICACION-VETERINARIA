import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClienteVentana extends JFrame {
    private JPanel ventanaCliente;
    private JButton AGENDARCITAButton;
    private JButton cerrarSesionButton;
    private int idUsuario;

    public ClienteVentana(int idUsuario) {
        this.idUsuario = idUsuario;


        setTitle("Veterinaria - Cliente");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ventanaCliente = new JPanel(new BorderLayout());
        ventanaCliente.setBackground(new Color(240, 248, 255));
        setContentPane(ventanaCliente);


        JLabel tituloLabel = new JLabel("Bienvenido a la Veterinaria", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloLabel.setForeground(new Color(34, 85, 119));
        ventanaCliente.add(tituloLabel, BorderLayout.NORTH);


        JPanel mensajePanel = new JPanel();
        mensajePanel.setBackground(new Color(240, 248, 255));
        JLabel mensajeLabel = new JLabel("<html><div style='text-align: center;'>"
                + "Somos una clínica veterinaria comprometida con el bienestar de tu mascota. "
                + "Ofrecemos servicios de veterinaria general, estética y odontología, asegurando el mejor cuidado y atención. "
                + "Confía en nosotros para cuidar a tu amigo peludo.</div></html>");
        mensajeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        mensajeLabel.setForeground(new Color(54, 54, 54));
        mensajePanel.add(mensajeLabel);

        ventanaCliente.add(mensajePanel, BorderLayout.CENTER);


        JPanel botonesPanel = new JPanel();
        botonesPanel.setLayout(new BoxLayout(botonesPanel, BoxLayout.Y_AXIS));
        botonesPanel.setBackground(new Color(240, 248, 255));


        AGENDARCITAButton = crearBoton("Agendar Cita");
        AGENDARCITAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HistorialCitasVentana historialCitasVentana = new HistorialCitasVentana("CLIENTE", 1);
                new SolicitudCitaVentana(idUsuario, historialCitasVentana);
            }
        });


        cerrarSesionButton = crearBoton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });


        botonesPanel.add(Box.createVerticalStrut(30));
        botonesPanel.add(AGENDARCITAButton);
        botonesPanel.add(Box.createVerticalStrut(20));
        botonesPanel.add(cerrarSesionButton);

        ventanaCliente.add(botonesPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(200, 40));
        boton.setFocusPainted(false);
        boton.setBackground(new Color(34, 119, 160));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 84, 110), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(28, 102, 136));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(34, 119, 160));
            }
        });

        return boton;
    }

    private void cerrarSesion() {
        dispose();
        LOGIN loginVentana = new LOGIN();
        loginVentana.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClienteVentana(1);
            }
        });
    }
}
