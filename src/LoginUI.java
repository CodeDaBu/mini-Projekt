import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.example.miniprojekt.db.DatabaseManager;

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
                int userId = getUserId(username);  // pridobi ID iz baze
                dispose();

                if (username.equalsIgnoreCase("admin")) {
                    new GlavniWindowAdmin().setVisible(true);
                } else {
                    new GlavniWindow(userId).setVisible(true);  // posreduj ID
                }
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Nepravilno uporabniško ime ali geslo", "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private int getUserId(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri iskanju ID-ja uporabnika: " + e.getMessage());
        }
        return -1;
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
