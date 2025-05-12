import com.example.miniprojekt.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class AddUserWindow extends JFrame {

    public AddUserWindow(UserManagementWindow parent) {
        setTitle("Dodaj novega uporabnika");
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(7, 2));

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();  // Dodamo geslo
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField krajIdField = new JTextField();

        add(new JLabel("Uporabniško ime:")); add(usernameField);
        add(new JLabel("Geslo:")); add(passwordField);
        add(new JLabel("Email:")); add(emailField);
        add(new JLabel("Telefon:")); add(phoneField);
        add(new JLabel("Kraj ID:")); add(krajIdField);

        JButton saveButton = new JButton("Shrani");
        saveButton.addActionListener(e -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                String sql = "INSERT INTO users (username, password, email, phone, kraj_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, usernameField.getText());
                stmt.setString(2, passwordField.getText());
                stmt.setString(3, emailField.getText());
                stmt.setString(4, phoneField.getText());
                stmt.setInt(5, Integer.parseInt(krajIdField.getText()));
                Timestamp now = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(6, now); // created_at
                stmt.setTimestamp(7, now); // updated_at

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Uporabnik uspešno dodan!");
                dispose();
                parent.napolniUporabnike(); // osveži seznam uporabnikov
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Napaka: " + ex.getMessage());
            }
        });

        add(new JLabel("")); // prazen prostor
        add(saveButton);

        setVisible(true);
    }
}
