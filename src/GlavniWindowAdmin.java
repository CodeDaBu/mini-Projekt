import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import com.example.miniprojekt.db.DatabaseManager;

public class GlavniWindowAdmin extends JFrame {

    private DefaultTableModel model;
    private JTable table;

    public GlavniWindowAdmin() {
        setTitle("Admin - Seznam radijskih postaj");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Ime", "Frekvenca", "Kanal", "Velja do", "Telefon", "Email"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton addButton = new JButton("Dodaj novo postajo");
        addButton.addActionListener(e -> new DodajPostajoWindow(this));

        JButton switchToUsersButton = new JButton("Poglej uporabnike");
        switchToUsersButton.addActionListener(e -> {
            setVisible(false);
            new UserManagementWindow().setVisible(true);
        });

        JButton refreshButton = new JButton("Osveži");
        refreshButton.addActionListener(e -> napolniTabelo());

        JButton saveButton = new JButton("Shrani spremembe");
        saveButton.addActionListener(e -> saveChangesToDatabase(table));

        JButton logoutButton = new JButton("Odjava");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });

        // --- NOVI GUMB ZA BRISANJE POSTAJE ---
        JButton deleteButton = new JButton("Izbriši izbrano postajo");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Izberi postajo za brisanje.");
                return;
            }
            int radioId = (Integer) table.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ali si prepričan, da želiš izbrisati radijsko postajo z ID: " + radioId + "?",
                    "Potrditev brisanja", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (izbrisiRadioPostajo(radioId)) {
                    JOptionPane.showMessageDialog(this, "Radijska postaja uspešno izbrisana.");
                    napolniTabelo(); // osveži tabelo
                } else {
                    JOptionPane.showMessageDialog(this, "Brisanje ni uspelo.");
                }
            }
        });

        bottomPanel.add(addButton);
        bottomPanel.add(switchToUsersButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(logoutButton);
        bottomPanel.add(deleteButton);  // dodan gumb tukaj

        panel.add(bottomPanel, BorderLayout.SOUTH);
        add(panel);

        napolniTabelo();
    }

    public void napolniTabelo() {
        model.setRowCount(0);
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

    private void saveChangesToDatabase(JTable table) {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (int row = 0; row < table.getRowCount(); row++) {
                int radioId = (Integer) table.getValueAt(row, 0);
                String ime = (String) table.getValueAt(row, 1);
                double frekvenca = (Double) table.getValueAt(row, 2);
                String kanal = (String) table.getValueAt(row, 3);
                Date validUntil = (Date) table.getValueAt(row, 4);
                String phone = (String) table.getValueAt(row, 5);
                String email = (String) table.getValueAt(row, 6);

                String sql = "UPDATE radio SET ime = ?, frekvenca = ?, channel = ?, valid_until = ?, phone = ?, email = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, ime);
                stmt.setDouble(2, frekvenca);
                stmt.setString(3, kanal);
                stmt.setDate(4, validUntil);
                stmt.setString(5, phone);
                stmt.setString(6, email);
                stmt.setInt(7, radioId);

                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Spremembe so bile shranjene!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri shranjevanju sprememb: " + e.getMessage());
        }
    }

    // --- NOVA METODA ZA KLIC SQL FUNKCIJE ZA BRISANJE ---
    private boolean izbrisiRadioPostajo(int radioId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT izbrisi_radio_postajo(?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, radioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri brisanju radijske postaje: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GlavniWindowAdmin().setVisible(true));
    }
}
