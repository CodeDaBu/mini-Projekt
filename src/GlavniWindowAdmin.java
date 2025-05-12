import com.example.miniprojekt.db.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GlavniWindowAdmin extends JFrame {

    public GlavniWindowAdmin() {
        setTitle("Admin - Seznam radijskih postaj");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Tabela
        String[] columnNames = {"ID", "Ime", "Frekvenca", "Kanal", "Velja do", "Telefon", "Email"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Logout gumb
        JButton logoutButton = new JButton("Odjava");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginUI();
        });

        // Dodaj gumb
        JButton addButton = new JButton("Dodaj novo postajo");
        addButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Funkcija dodajanja še ni implementirana.");
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        bottomPanel.add(logoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        // Napolni tabelo
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM radio";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getDouble("frekvenca"),
                        rs.getString("channel"),
                        rs.getDate("valid_until"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri nalaganju podatkov: " + e.getMessage());
        }
    }
}
