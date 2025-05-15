import javax.swing.*;
import java.awt.*;
import com.example.miniprojekt.db.DatabaseManager;

public class LoginUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Login login;

    public LoginUI() {
        setTitle("Prijava");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Uporabniško ime:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Geslo:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Prijavi se");
        registerButton = new JButton("Registriraj se");

        panel.add(loginButton);
        panel.add(registerButton);

        add(panel, BorderLayout.CENTER);

        login = new Login();

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            int userId = login.loginUser(username, password);

            if (userId != -1) {
                dispose();  // zapri prijavno okno
                if (username.equalsIgnoreCase("admin")) {
                    new GlavniWindowAdmin().setVisible(true);
                } else {
                    new GlavniWindow(userId).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nepravilno uporabniško ime ali geslo", "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            JTextField newUsername = new JTextField();
            JPasswordField newPassword = new JPasswordField();
            Object[] message = {
                    "Novo uporabniško ime:", newUsername,
                    "Novo geslo:", newPassword
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Registracija", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String newUser = newUsername.getText();
                String newPass = new String(newPassword.getPassword());

                boolean success = login.registerUser(newUser, newPass);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Registracija uspešna!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Registracija ni uspela. Uporabnik morda že obstaja.", "Napaka", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
