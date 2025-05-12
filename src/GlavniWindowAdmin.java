import com.example.miniprojekt.db.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GlavniWindowAdmin extends JFrame {

    private DefaultTableModel model;
    private JTable table;

    public GlavniWindowAdmin() {
        setTitle("Admin - Seznam radijskih postaj");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Tabela
        String[] columnNames = {"ID", "Ime", "Frekvenca", "Kanal", "Velja do", "Telefon", "Email"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Gumbi za preklop med uporabniki in postajami
        JPanel bottomPanel = new JPanel();

        JButton addButton = new JButton("Dodaj novo postajo");
        addButton.addActionListener(e -> new DodajPostajoWindow(this));

        JButton switchToUsersButton = new JButton("Poglej uporabnike");
        switchToUsersButton.addActionListener(e -> {
            setVisible(false); // Skrij to okno
            new UserManagementWindow().setVisible(true); // Odpri in prikaži okno za uporabnike
        });

        JButton refreshButton = new JButton("Osveži");
        refreshButton.addActionListener(e -> napolniTabelo());

        JButton saveButton = new JButton("Shrani spremembe");
        saveButton.addActionListener(e -> saveChangesToDatabase(table));

        JButton logoutButton = new JButton("Odjava");
        logoutButton.addActionListener(e -> {
            dispose(); // Zapri trenutno okno
            new LoginUI().setVisible(true); // Odpri login okno
        });

        bottomPanel.add(addButton);
        bottomPanel.add(switchToUsersButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(logoutButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        add(panel);

        napolniTabelo(); // Naloži podatke ob zagonu
    }

    // Metoda za nalaganje podatkov v tabelo
    public void napolniTabelo() {
        model.setRowCount(0); // Počisti tabelo

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

    // Metoda za shranjevanje sprememb v bazo
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

    // Glavni razred za zagon aplikacije
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GlavniWindowAdmin().setVisible(true));
    }
}
