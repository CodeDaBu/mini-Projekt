import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import com.example.miniprojekt.db.DatabaseManager;


public class UserManagementWindow extends JFrame {

    private DefaultTableModel model;
    private JTable table;

    public UserManagementWindow() {
        setTitle("Seznam uporabnikov");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Uporabniško ime", "Email", "Telefon", "Geslo", "Spremeni Geslo"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Nazaj");
        backButton.addActionListener(e -> {
            dispose();
            new GlavniWindowAdmin().setVisible(true);
        });

        JButton addUserButton = new JButton("Dodaj uporabnika");
        addUserButton.addActionListener(e -> dodajUporabnika());

        JButton refreshButton = new JButton("Osveži");
        refreshButton.addActionListener(e -> napolniUporabnike());

        JButton saveButton = new JButton("Shrani spremembe");
        saveButton.addActionListener(e -> saveChangesToDatabase(table));

        bottomPanel.add(backButton);
        bottomPanel.add(addUserButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(saveButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        add(panel);

        napolniUporabnike();
    }

    public void napolniUporabnike() {
        model.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri nalaganju uporabnikov: " + e.getMessage());
        }
    }

    private void saveChangesToDatabase(JTable table) {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (int row = 0; row < table.getRowCount(); row++) {
                int userId = (Integer) table.getValueAt(row, 0);
                String username = (String) table.getValueAt(row, 1);
                String email = (String) table.getValueAt(row, 2);
                String phone = (String) table.getValueAt(row, 3);
                String password = (String) table.getValueAt(row, 4);

                String sql = "UPDATE users SET username = ?, email = ?, phone = ?, password = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.setString(4, password);
                stmt.setInt(5, userId);

                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Spremembe uporabnikov so bile shranjene!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri shranjevanju sprememb uporabnikov: " + e.getMessage());
        }
    }

    private void dodajUporabnika() {
        String username = JOptionPane.showInputDialog(this, "Vnesite uporabniško ime:");
        String email = JOptionPane.showInputDialog(this, "Vnesite email:");
        String phone = JOptionPane.showInputDialog(this, "Vnesite telefon:");
        String password = JOptionPane.showInputDialog(this, "Vnesite geslo:");

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO users (username, email, phone, password) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Uporabnik je bil uspešno dodan.");
            napolniUporabnike();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri dodajanju uporabnika: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserManagementWindow().setVisible(true));
    }
}
