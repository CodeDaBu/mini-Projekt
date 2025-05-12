import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private Login login;  // Povezava na Login razred

    public LoginUI() {
        setTitle("Prijava");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Uporabniško ime:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Geslo:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Prijavi se");
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        // Inicializiraj Login objekt
        login = new Login();

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Preveri, ali je uporabnik veljaven
                if (login.isValidUser(username, password)) {
                    openMainWindow();  // Odpri Glavni Window
                    dispose();  // Zapri trenutni login UI
                } else {
                    JOptionPane.showMessageDialog(LoginUI.this, "Nepravilno uporabniško ime ali geslo", "Napaka", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    // Odpri Glavni Window
    private void openMainWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GlavniWindow().setVisible(true);  // Zagotovi, da okno teče v svojem threadu
            }
        });
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
