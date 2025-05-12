import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.example.miniprojekt.db.DatabaseManager;

public class GlavniWindow extends JFrame {

    public GlavniWindow() {
        setTitle("Seznam radijskih postaj");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Tabela
        String[] columnNames = {"Ime"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // onemogoči urejanje
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Logout gumb
        JButton logoutButton = new JButton("Odjava");
        logoutButton.addActionListener(e -> {
            dispose(); // zapri okno
            new LoginUI(); // pokaži prijavno okno
        });
        panel.add(logoutButton, BorderLayout.SOUTH);

        add(panel);

        // Napolni tabelo z imeni postaj
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT id, ime, frekvenca, channel, valid_until, phone, email FROM radio";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("ime");
                model.addRow(new Object[]{name});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri branju radijskih postaj: " + e.getMessage());
        }

        // Dvoklik za prikaz podrobnosti
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    String name = (String) model.getValueAt(row, 0);
                    showStationDetails(name);
                }
            }
        });
    }

    private void showStationDetails(String name) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM radio WHERE ime = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String message = String.format(
                        "Ime: %s\nFrekvenca: %.2f\nKanal: %s\nVelja do: %s\nTelefon: %s\nEmail: %s",
                        rs.getString("ime"),
                        rs.getDouble("frekvenca"),
                        rs.getString("channel"),
                        rs.getDate("valid_until"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                JOptionPane.showMessageDialog(this, message, "Podrobnosti postaje", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri pridobivanju podatkov: " + e.getMessage());
        }
    }
}
