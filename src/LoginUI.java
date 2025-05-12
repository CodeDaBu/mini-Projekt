import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Login login;

    public LoginUI() {
        setTitle("Prijava");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Uporabniško ime:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Geslo:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Prijavi se");
        panel.add(new JLabel());  // prazno polje
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        login = new Login();

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (login.isValidUser(username, password)) {
                dispose();
                if (username.equalsIgnoreCase("admin")) {
                    new GlavniWindowAdmin().setVisible(true);
                } else {
                    new GlavniWindow().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Nepravilno uporabniško ime ali geslo", "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
